import http from 'k6/http';
import exec from 'k6/execution';
import { check } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const BASE_URL =
    __ENV.BASE_URL || 'http://172.30.1.100:9200';

const LOGIN_PATH =
    __ENV.LOGIN_PATH || '/login';

const PASSWORD =
    __ENV.TEST_PASSWORD || 'qwer1234';

const loginDuration = new Trend(
    'login_duration',
    true
);

const loginFailureRate = new Rate(
    'login_failure_rate'
);

const loginSuccessCount = new Counter(
    'login_success_count'
);

const loginFailureCount = new Counter(
    'login_failure_count'
);

export const options = {
    scenarios: {
        concurrent_login_100: {
            /*
             * VU마다 정해진 iteration 수만큼 실행한다.
             *
             * VU 100명 × iteration 1회
             * = 로그인 요청 총 100회
             */
            executor: 'per-vu-iterations',
            vus: 1000,
            iterations: 1,
            maxDuration: '30s',
            gracefulStop: '0s',
        },
    },

    thresholds: {
        /*
         * 로그인 실패율 1% 미만
         */
        login_failure_rate: [
            'rate<0.01',
        ],

        /*
         * 로그인 응답시간
         */
        login_duration: [
            'p(90)<500',
            'p(95)<1000',
            'p(99)<2000',
        ],

        /*
         * 전체 HTTP 요청 실패율
         */
        http_req_failed: [
            'rate<0.01',
        ],

        /*
         * 모든 check 성공률
         */
        checks: [
            'rate>0.99',
        ],
    },
};

function createAccount(vuId) {
    const number = String(vuId);

    return {
        userEmail: `test${number}@gmail.com`,
        userPassword: PASSWORD,
    };
}

function extractAccessToken(response) {
    try {
        const body = response.json();

        /*
         * 프로젝트 응답 구조에 따라 아래 후보 중 하나를 사용한다.
         */
        return (
            body.accessToken ??
            body.token ??
            body.data?.accessToken ??
            body.data?.token ??
            null
        );
    } catch (_) {
        return null;
    }
}

export default function () {
    /*
     * idInTest:
     *
     * VU 1   → loadtest001@test.com
     * VU 2   → loadtest002@test.com
     * ...
     * VU 100 → loadtest100@test.com
     */
    const vuId = exec.vu.idInTest;
    const account = createAccount(vuId);

    const payload = JSON.stringify({
        userEmail: account.userEmail,
        userPassword: account.userPassword,
    });

    console.log(`URL: ${BASE_URL}${LOGIN_PATH}`);
    console.log(`BODY: ${payload}`);

    const response = http.post(
        `${BASE_URL}${LOGIN_PATH}`,
        payload,
        {
            headers: {
                'Content-Type': 'application/json',
                Accept: 'application/json',
            },
            tags: {
                name: 'POST /login',
                scenario_type: 'concurrent-login',
            },
            timeout: '15s',
        }
    );

    loginDuration.add(response.timings.duration);

    const accessToken = extractAccessToken(response);

    const success = check(response, {
        '로그인 HTTP 상태 성공': (r) =>
            [200, 201].includes(r.status),

        '응답이 JSON 형식': (r) => {
            const contentType =
                r.headers['Content-Type'] || '';

            return contentType.includes(
                'application/json'
            );
        },

        'Access Token 존재': () =>
            Boolean(accessToken),
    });

    loginFailureRate.add(!success);

    if (success) {
        loginSuccessCount.add(1);
        return;
    }

    loginFailureCount.add(1);

    /*
     * 실패한 계정만 출력한다.
     * 성공 응답 100개를 전부 출력하면 결과 확인이 어려워진다.
     */
    console.error(
        JSON.stringify({
            vuId,
            email: account.email,
            status: response.status,
            duration: response.timings.duration,
            body: response.body,
        })
    );
}