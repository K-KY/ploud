### strength 4
````
  █ THRESHOLDS 

    checks
    ✓ 'rate>0.99' rate=100.00%

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    login_duration
    ✗ 'p(90)<500' p(90)=878.69ms
    ✓ 'p(95)<1000' p(95)=922.51ms
    ✓ 'p(99)<2000' p(99)=996.11ms

    login_failure_rate
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......: 3000    1540.020513/s
    checks_succeeded...: 100.00% 3000 out of 3000
    checks_failed......: 0.00%   0 out of 3000

    ✓ 로그인 HTTP 상태 성공
    ✓ 응답이 JSON 형식
    ✓ Access Token 존재

    CUSTOM
    login_duration.................: avg=401.36ms min=10.21ms med=301.7ms max=1.07s p(90)=878.69ms p(95)=922.51ms
    login_failure_rate.............: 0.00%  0 out of 1000
    login_success_count............: 1000   513.340171/s

    HTTP
    http_req_duration..............: avg=401.36ms min=10.21ms med=301.7ms max=1.07s p(90)=878.69ms p(95)=922.51ms
      { expected_response:true }...: avg=401.36ms min=10.21ms med=301.7ms max=1.07s p(90)=878.69ms p(95)=922.51ms
    http_req_failed................: 0.00%  0 out of 1000
    http_reqs......................: 1000   513.340171/s

    EXECUTION
    iteration_duration.............: avg=1.02s    min=35.98ms med=1.08s   max=1.93s p(90)=1.74s    p(95)=1.82s   
    iterations.....................: 1000   513.340171/s
    vus............................: 7      min=7         max=574 
    vus_max........................: 1000   min=1000      max=1000

    NETWORK
    data_received..................: 919 kB 472 kB/s
    data_sent......................: 216 kB 111 kB/s

````

### strength 10
````
█ THRESHOLDS 

    checks
    ✓ 'rate>0.99' rate=100.00%

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    login_duration
    ✗ 'p(90)<500' p(90)=10.5s
    ✗ 'p(95)<1000' p(95)=10.7s
    ✗ 'p(99)<2000' p(99)=10.92s

    login_failure_rate
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......: 3000    260.219063/s
    checks_succeeded...: 100.00% 3000 out of 3000
    checks_failed......: 0.00%   0 out of 3000

    ✓ 로그인 HTTP 상태 성공
    ✓ 응답이 JSON 형식
    ✓ Access Token 존재

    CUSTOM
    login_duration.................: avg=6.27s min=338.21ms med=6.56s max=11.03s p(90)=10.5s  p(95)=10.7s 
    login_failure_rate.............: 0.00%  0 out of 1000
    login_success_count............: 1000   86.739688/s

    HTTP
    http_req_duration..............: avg=6.27s min=338.21ms med=6.56s max=11.03s p(90)=10.5s  p(95)=10.7s 
      { expected_response:true }...: avg=6.27s min=338.21ms med=6.56s max=11.03s p(90)=10.5s  p(95)=10.7s 
    http_req_failed................: 0.00%  0 out of 1000
    http_reqs......................: 1000   86.739688/s

    EXECUTION
    iteration_duration.............: avg=6.81s min=351.56ms med=7.22s max=11.52s p(90)=11.15s p(95)=11.32s
    iterations.....................: 1000   86.739688/s
    vus............................: 139    min=139       max=961 
    vus_max........................: 1000   min=1000      max=1000

    NETWORK
    data_received..................: 919 kB 80 kB/s
    data_sent......................: 216 kB 19 kB/s````