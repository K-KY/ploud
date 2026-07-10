import http from 'k6/http';
import exec from 'k6/execution';
import { check, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

let isolate = 'http://172.30.1.100:9200';
let local = 'http://localhost:8080';
const BASE_URL = (__ENV.BASE_URL || local).replace(/\/$/, '');
const LOGIN_PATH = __ENV.LOGIN_PATH || '/login';
const LOGIN_USER_PREFIX = __ENV.LOGIN_USER_PREFIX || 'test';
const LOGIN_DOMAIN = __ENV.LOGIN_DOMAIN || 'gmail.com';
const LOGIN_START = Number(__ENV.LOGIN_START || 1);
const LOGIN_END = Number(__ENV.LOGIN_END || 1000);
const LOGIN_PASSWORD = __ENV.LOGIN_PASSWORD || __ENV.TEST_PASSWORD || 'qwer1234';
const DIR_PATH = (__ENV.DIR_PATH || 'depth-1,depth-2,depth-3')
  .split(',')
  .map((name) => name.trim())
  .filter(Boolean);
const READ_VUS = Number(__ENV.READ_VUS || accountCount());
const READ_ITERATIONS = Number(__ENV.READ_ITERATIONS || 1);
const READ_MAX_DURATION = __ENV.READ_MAX_DURATION || '2m';
const REQUEST_TIMEOUT = __ENV.REQUEST_TIMEOUT || '30s';
const SUMMARY_PATH = __ENV.SUMMARY || './race_read_summary.json';

const loginLatency = new Trend('login_latency', true);
const rootLookupLatency = new Trend('root_lookup_latency', true);
const directoryLookupLatency = new Trend('directory_lookup_latency', true);
const finalFileLookupLatency = new Trend('final_file_lookup_latency', true);
const traverseTotalLatency = new Trend('traverse_total_latency', true);

const loginRequests = new Counter('login_requests');
const rootLookupRequests = new Counter('root_lookup_requests');
const directoryLookupRequests = new Counter('directory_lookup_requests');
const finalFileLookupRequests = new Counter('final_file_lookup_requests');
const traverseRequests = new Counter('traverse_requests');

const loginFailures = new Rate('login_failures');
const traverseFailures = new Rate('traverse_failures');
const directoryLookupFailures = new Rate('directory_lookup_failures');
const finalFileLookupFailures = new Rate('final_file_lookup_failures');

export const options = {
  scenarios: {
    read_depth3_with_1000_users: {
      executor: 'per-vu-iterations',
      vus: READ_VUS,
      iterations: READ_ITERATIONS,
      maxDuration: READ_MAX_DURATION,
      gracefulStop: '0s',
      exec: 'traverseToLastDepth',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    login_failures: ['rate<0.01'],
    traverse_failures: ['rate<0.01'],
    directory_lookup_failures: ['rate<0.01'],
    final_file_lookup_failures: ['rate<0.01'],
    checks: ['rate>0.99'],
  },
};

if (DIR_PATH.length === 0) {
  throw new Error('DIR_PATH가 비어 있습니다. 예: DIR_PATH=depth-1,depth-2,depth-3');
}

if (READ_VUS > accountCount()) {
  throw new Error(`READ_VUS=${READ_VUS}가 로그인 계정 수=${accountCount()}보다 큽니다.`);
}

export function setup() {
  const accounts = loginAccounts();
  const tokens = accounts.map((email) => ({ email, token: login(email) }));

  if (tokens.length === 0) {
    throw new Error('로그인 가능한 계정이 없습니다. LOGIN_START/LOGIN_END 설정을 확인하세요.');
  }

  return { tokens };
}

export function traverseToLastDepth(data) {
  const account = accountFor(data);
  const headers = authHeaders(account.token);
  const started = Date.now();
  let failed = false;
  let currentDirSeq;

  group('read: root directory', () => {
    rootLookupRequests.add(1);
    const rootRes = timed(rootLookupLatency, () => http.get(
      `${BASE_URL}/api/v1/dirs`,
      {
        headers,
        timeout: REQUEST_TIMEOUT,
        tags: { step: 'root_lookup' },
      },
    ));

    const ok = check(rootRes, {
      'root directory status is 200': (res) => res.status === 200,
      'root directory response has dirs': (res) => Array.isArray(safeJson(res).dirs),
    });

    directoryLookupFailures.add(!ok);
    if (!ok) {
      failed = true;
      logFailure('root_lookup', account.email, rootRes);
      return;
    }

    currentDirSeq = findChildDirSeq(rootRes, DIR_PATH[0]);
    if (!currentDirSeq) {
      failed = true;
      directoryLookupFailures.add(true);
      console.error(`root_lookup failed email=${account.email} missing=${DIR_PATH[0]} body=${shortBody(rootRes)}`);
    }
  });

  for (let depthIndex = 1; !failed && depthIndex < DIR_PATH.length; depthIndex += 1) {
    const targetDirName = DIR_PATH[depthIndex];

    group(`read: ${targetDirName}`, () => {
      directoryLookupRequests.add(1);
      const dirRes = timed(directoryLookupLatency, () => http.get(
        `${BASE_URL}/api/v1/dirs/${currentDirSeq}`,
        {
          headers,
          timeout: REQUEST_TIMEOUT,
          tags: { step: 'directory_lookup', depth: String(depthIndex + 1) },
        },
      ));

      const ok = check(dirRes, {
        'child directory status is 200': (res) => res.status === 200,
        'child directory response has dirs': (res) => Array.isArray(safeJson(res).dirs),
      });

      directoryLookupFailures.add(!ok);
      if (!ok) {
        failed = true;
        logFailure('directory_lookup', account.email, dirRes);
        return;
      }

      currentDirSeq = findChildDirSeq(dirRes, targetDirName);
      if (!currentDirSeq) {
        failed = true;
        directoryLookupFailures.add(true);
        console.error(`directory_lookup failed email=${account.email} missing=${targetDirName} body=${shortBody(dirRes)}`);
      }
    });
  }

  if (!failed) {
    group('read: final directory files', () => {
      finalFileLookupRequests.add(1);
      const fileRes = timed(finalFileLookupLatency, () => http.get(
        `${BASE_URL}/api/v1/files/${currentDirSeq}`,
        {
          headers,
          timeout: REQUEST_TIMEOUT,
          tags: { step: 'final_file_lookup' },
        },
      ));

      const ok = check(fileRes, {
        'final file lookup status is 200': (res) => res.status === 200,
        'final file lookup response is json': (res) => contentType(res).includes('application/json'),
      });

      finalFileLookupFailures.add(!ok);
      if (!ok) {
        failed = true;
        logFailure('final_file_lookup', account.email, fileRes);
      }
    });
  }

  traverseRequests.add(1);
  traverseFailures.add(failed);
  traverseTotalLatency.add(Date.now() - started);
}

function loginAccounts() {
  const accounts = [];
  for (let i = LOGIN_START; i <= LOGIN_END; i += 1) {
    accounts.push(`${LOGIN_USER_PREFIX}${i}@${LOGIN_DOMAIN}`);
  }
  return accounts;
}

function accountCount() {
  return Math.max(0, LOGIN_END - LOGIN_START + 1);
}

function login(email) {
  loginRequests.add(1);
  const loginRes = timed(loginLatency, () => http.post(
    `${BASE_URL}${LOGIN_PATH}`,
    JSON.stringify({
      userEmail: email,
      userPassword: LOGIN_PASSWORD,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      timeout: REQUEST_TIMEOUT,
      tags: { step: 'login' },
    },
  ));

  const accessToken = extractAccessToken(loginRes);
  const ok = check(loginRes, {
    'login status is 200': (res) => res.status === 200,
    'login accessToken exists': () => Boolean(accessToken),
  });

  loginFailures.add(!ok);
  if (!ok) {
    throw new Error(`로그인 실패: ${email}, status=${loginRes.status}, body=${shortBody(loginRes)}`);
  }

  return accessToken;
}

function accountFor(data) {
  if (!data.tokens || data.tokens.length === 0) {
    throw new Error('로그인 토큰이 없습니다.');
  }

  const index = (exec.vu.idInTest - 1) % data.tokens.length;
  return data.tokens[index];
}

function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
    Accept: 'application/json',
  };
}

function extractAccessToken(response) {
  const body = safeJson(response);
  return body.accessToken
    || body.token
    || (body.data && body.data.accessToken)
    || (body.data && body.data.token)
    || null;
}

function findChildDirSeq(response, dirName) {
  const body = safeJson(response);
  const dirs = Array.isArray(body.dirs) ? body.dirs : [];
  const found = dirs.find((dir) => dir && dir.dirName === dirName);
  return found ? found.dirSeq : null;
}

function safeJson(response) {
  try {
    return response.json();
  } catch (_) {
    return {};
  }
}

function contentType(response) {
  return response.headers['Content-Type'] || response.headers['content-type'] || '';
}

function timed(trend, fn) {
  const started = Date.now();
  const result = fn();
  trend.add(Date.now() - started);
  return result;
}

function logFailure(step, email, response) {
  console.error(`${step} failed email=${email} status=${response.status} body=${shortBody(response)}`);
}

function shortBody(response) {
  return String(response.body || '').slice(0, 500);
}

function metricCount(data, name) {
  return data.metrics[name] && data.metrics[name].values.count
    ? data.metrics[name].values.count
    : 0;
}

function metricRate(data, name) {
  return data.metrics[name] && data.metrics[name].values.rate
    ? data.metrics[name].values.rate
    : 0;
}

function metricValues(data, name) {
  return data.metrics[name] && data.metrics[name].values
    ? data.metrics[name].values
    : {};
}

function thresholdLines(data) {
  const lines = ['  THRESHOLDS'];
  const thresholdMetrics = Object.keys(data.metrics)
    .filter((name) => data.metrics[name].thresholds);

  if (thresholdMetrics.length === 0) {
    lines.push('    no thresholds');
    return lines.join('\n');
  }

  thresholdMetrics.forEach((name) => {
    lines.push(`    ${name}`);
    Object.keys(data.metrics[name].thresholds).forEach((threshold) => {
      const thresholdResult = data.metrics[name].thresholds[threshold];
      const mark = thresholdResult.ok ? '✓' : '✗';
      lines.push(`    ${mark} '${threshold}'`);
    });
    lines.push('');
  });

  return lines.join('\n').trimEnd();
}

function trendLine(data, name, label) {
  const values = metricValues(data, name);
  return [
    `    ${label.padEnd(32)}:`,
    `avg=${formatMs(values.avg)}`,
    `min=${formatMs(values.min)}`,
    `med=${formatMs(values.med)}`,
    `max=${formatMs(values.max)}`,
    `p(90)=${formatMs(values['p(90)'])}`,
    `p(95)=${formatMs(values['p(95)'])}`,
    `p(99)=${formatMs(values['p(99)'])}`,
  ].join(' ');
}

function rateLine(data, name, label) {
  const values = metricValues(data, name);
  const rate = values.rate === undefined ? 0 : values.rate;
  const passes = values.passes === undefined ? 0 : values.passes;
  const fails = values.fails === undefined ? 0 : values.fails;
  return `    ${label.padEnd(32)}: ${formatPercent(rate)} ${fails} failed / ${passes + fails} total`;
}

function counterLine(data, name, label) {
  const values = metricValues(data, name);
  const count = values.count === undefined ? 0 : values.count;
  const rate = values.rate === undefined ? 0 : values.rate;
  return `    ${label.padEnd(32)}: ${count} ${formatNumber(rate)}/s`;
}

function formatMs(value) {
  if (value === undefined || value === null || Number.isNaN(value)) {
    return '0ms';
  }
  if (value >= 1000) {
    return `${formatNumber(value / 1000)}s`;
  }
  return `${formatNumber(value)}ms`;
}

function formatPercent(value) {
  return `${formatNumber(value * 100)}%`;
}

function formatNumber(value) {
  if (value === undefined || value === null || Number.isNaN(value)) {
    return '0';
  }
  return Number(value).toFixed(3).replace(/\.?0+$/, '');
}

function textSummary(data, summary) {
  return [
    '',
    thresholdLines(data),
    '',
    '  TOTAL RESULTS',
    '',
    '  REQUEST RATE',
    counterLine(data, 'login_requests', 'login_requests'),
    counterLine(data, 'traverse_requests', 'traverse_requests'),
    counterLine(data, 'root_lookup_requests', 'root_lookup_requests'),
    counterLine(data, 'directory_lookup_requests', 'directory_lookup_requests'),
    counterLine(data, 'final_file_lookup_requests', 'final_file_lookup_requests'),
    counterLine(data, 'http_reqs', 'http_reqs'),
    '',
    '  LATENCY',
    trendLine(data, 'login_latency', 'login_latency'),
    trendLine(data, 'root_lookup_latency', 'root_lookup_latency'),
    trendLine(data, 'directory_lookup_latency', 'directory_lookup_latency'),
    trendLine(data, 'final_file_lookup_latency', 'final_file_lookup_latency'),
    trendLine(data, 'traverse_total_latency', 'traverse_total_latency'),
    trendLine(data, 'http_req_duration', 'http_req_duration'),
    '',
    '  FAILURES',
    rateLine(data, 'login_failures', 'login_failures'),
    rateLine(data, 'traverse_failures', 'traverse_failures'),
    rateLine(data, 'directory_lookup_failures', 'directory_lookup_failures'),
    rateLine(data, 'final_file_lookup_failures', 'final_file_lookup_failures'),
    rateLine(data, 'http_req_failed', 'http_req_failed'),
    '',
    '  TEST INFO',
    `    baseUrl                         : ${summary.baseUrl}`,
    `    accounts                        : ${summary.accounts.start}..${summary.accounts.end} (${summary.accounts.count})`,
    `    directoryPath                   : ${summary.directoryPath.join('/')}`,
    '',
  ].join('\n');
}
