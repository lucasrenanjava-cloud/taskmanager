$token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJsdWNhc0BlbWFpbC5jb20iLCJpYXQiOjE3NzU4MDM1NDMsImV4cCI6MTc3NTg4OTk0M30.YYqiQEA3p50kfeh2YhvhwJ0EAhfzoM0kEH2osz-PlV7UhcjMTb7EhkxPCdv-uqUe"
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Test 5: GET /tarefas?page=0&size=10
Write-Host "=== Test 5: GET /tarefas?page=0&size=10 ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas?page=0&size=10" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 6: GET /tarefas/{id}
$taskId = "1cc11205-19d4-4f8e-8cb6-132fadc1a166"
Write-Host "=== Test 6: GET /tarefas/$taskId ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/$taskId" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 7: GET /tarefas/filtrar?status=PENDENTE
Write-Host "=== Test 7: GET /tarefas/filtrar?status=PENDENTE ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/filtrar?status=PENDENTE" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 8: GET /tarefas/filtrar?prioridade=ALTA
Write-Host "=== Test 8: GET /tarefas/filtrar?prioridade=ALTA ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/filtrar?prioridade=ALTA" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 9: GET /tarefas/atrasadas
Write-Host "=== Test 9: GET /tarefas/atrasadas ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/atrasadas" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 10: GET /tarefas/buscar?titulo=Spring
Write-Host "=== Test 10: GET /tarefas/buscar?titulo=Spring ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/buscar?titulo=Spring" -Method GET -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 11: PUT /tarefas/{id}
Write-Host "=== Test 11: PUT /tarefas/$taskId ===" -ForegroundColor Cyan
try {
    $body = @{
        titulo = "Estudar Spring Boot e JWT"
        descricao = "Revisar documentação completa"
        prioridade = "MEDIA"
        prazo = "2026-05-15"
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/$taskId" -Method PUT -Headers $headers -Body $body
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 12: PATCH /tarefas/{id}/status
Write-Host "=== Test 12: PATCH /tarefas/$taskId/status ===" -ForegroundColor Cyan
try {
    $body = @{
        status = "EM_ANDAMENTO"
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/$taskId/status" -Method PATCH -Headers $headers -Body $body
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# Test 13: DELETE /tarefas/{id}
Write-Host "=== Test 13: DELETE /tarefas/$taskId ===" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/tarefas/$taskId" -Method DELETE -Headers $headers
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}