#!/bin/bash

# 1. Generate a valid JWT token (Must have 3 parts separated by dots)
JWT=$(python3 -c '
import base64, json, time
header = base64.urlsafe_b64encode(json.dumps({"alg": "none", "typ": "JWT"}).encode()).decode().rstrip("=")
payload = base64.urlsafe_b64encode(json.dumps({"iss": "cmu.edu", "sub": "starlord", "exp": int(time.time()) + 3600}).encode()).decode().rstrip("=")
signature = "dummy_sig"
print(f"{header}.{payload}.{signature}")
')

echo "Generated JWT: $JWT"
echo "-----------------------------------"
echo "Waiting 30 seconds for services to initialize..."
sleep 30
echo "-----------------------------------"

# 2. Test Book Routing
echo "Testing Book Routing (Expected: 200 OK)..."
curl -s -i -X GET http://localhost:8081/books/9780134685991 \
     -H "X-Client-Type: Web" \
     -H "Authorization: Bearer $JWT" | head -n 1

# 3. Test Customer Routing
echo "Testing Customer Routing (Expected: 200 OK)..."
curl -s -i -X GET http://localhost:8081/customers/1 \
     -H "X-Client-Type: Web" \
     -H "Authorization: Bearer $JWT" | head -n 1

# 4. Test Related Books
echo "Testing Related Books (Expected: 200, 204, or 504 on timeout)..."
curl -s -i -X GET http://localhost:8081/books/9780134685991/related-books \
     -H "X-Client-Type: Web" \
     -H "Authorization: Bearer $JWT" | head -n 1

# 5. Test Kafka Registration
echo "Testing Customer Registration (Expected: 201 Created)..."
curl -s -i -X POST http://localhost:8081/customers \
     -H "X-Client-Type: Web" \
     -H "Authorization: Bearer $JWT" \
     -H "Content-Type: application/json" \
     -d '{
       "userId": "test_user_'"$RANDOM"'@example.com",
       "name": "Test User",
       "phone": "555-1234",
       "address": "123 Test St",
       "city": "Pittsburgh",
       "state": "PA",
       "zipcode": "15213"
     }' | head -n 1

echo "-----------------------------------"
echo "Check CRM Service logs for email sending: docker-compose logs crm-service"
