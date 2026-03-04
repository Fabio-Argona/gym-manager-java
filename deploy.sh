#!/bin/bash
set -e

# Configurações
EC2_HOST="34.225.230.255"
EC2_USER="ubuntu"
REMOTE_DIR="~/gym-manager"

# Copia a chave para /tmp para garantir permissões corretas no Linux/WSL
cp "$(dirname "$0")/gym-secret-key.pem" /tmp/gym-key.pem
chmod 600 /tmp/gym-key.pem
PEM_KEY="/tmp/gym-key.pem"

echo "==> Buildando o projeto Java..."
cd gym-manager-java
mvn clean package -DskipTests
cd ..

echo "==> Enviando arquivos para EC2..."
scp -i "$PEM_KEY" -o StrictHostKeyChecking=no \
  gym-manager-java/target/*.jar \
  gym-manager-java/docker-compose.yml \
  gym-manager-java/Dockerfile \
  "$EC2_USER@$EC2_HOST:$REMOTE_DIR/"

echo "==> Reiniciando aplicação na EC2..."
ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" << 'EOF'
  cd ~/gym-manager
  docker compose down
  docker compose up --build -d
  docker image prune -f
  echo "Deploy concluído!"
EOF

echo "==> Deploy finalizado com sucesso!"
