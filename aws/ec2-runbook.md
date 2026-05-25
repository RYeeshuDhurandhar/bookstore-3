# EC2 Deployment Runbook

## 1. SSH into each EC2 instance
Use the AWS Learner Lab key and connect to both EC2 instances created by CloudFormation.

## 2. Verify Docker is installed
```bash
docker --version
sudo systemctl status docker
```

If Docker is not running:

```bash
sudo systemctl start docker
```

## 3. Install MySQL-compatible client
Amazon Linux 2023 uses MariaDB packages.

```bash
sudo dnf install mariadb105-server -y
```

## 4. Connect to Aurora writer endpoint

```bash
mysql -h <aurora-writer-endpoint> -u <db-username> -p
```

Inside MySQL:

```sql
SOURCE aws/create_tables.sql;
```

Optional:

```sql
SOURCE aws/seed_data.sql;
```

## 5. Create environment file on EC2
Create a file: `/home/ec2-user/bookstore.env`

Example contents:

```env
DB_HOST=<aurora-writer-endpoint>
DB_PORT=3306
DB_NAME=bookstore
DB_USER=<db-username>
DB_PASSWORD=<db-password>
LLM_API_KEY=<llm-api-key>
LLM_MODEL=gemini-1.5-flash
LLM_TIMEOUT_MS=15000
SERVER_PORT=8080
```

Protect it:

```bash
chmod 600 /home/ec2-user/bookstore.env
```

## 6. Pull Docker image from Docker Hub

```bash
sudo docker pull <your-dockerhub-username>/bookstore-a1:latest
```

## 7. Run container

```bash
sudo docker run -d \
  --name bookstore \
  --restart unless-stopped \
  --env-file /home/ec2-user/bookstore.env \
  -p 80:8080 \
  <your-dockerhub-username>/bookstore-a1:latest
```

## 8. Test from inside EC2

```bash
curl http://localhost/status
```

Expected:

```
OK
```

## 9. Test through ALB
From your local machine:

```bash
curl http://<alb-dns-name>/status
```

## 10. Check logs if needed

```bash
sudo docker logs bookstore
```

## 11. Stop/remove container if needed

```bash
sudo docker stop bookstore
sudo docker rm bookstore
```
