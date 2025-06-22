#!/bin/bash

# Script to set up the MyInbox Telegram bot as a systemd service

BOT_DIR="$HOME/code/my-inbox-telegram-bot"
SECRETS_FILE="/etc/secrets/my-inbox-telegram-bot.conf"
SERVICE_FILE="/etc/systemd/system/my-inbox-telegram-bot.service"
JAVA_EXEC_PATH=$(which java)

# Secrets

sudo mkdir -p /etc/secrets

if [[ ! -f "$SECRETS_FILE" ]]; then
  echo "Creating secrets file: $SECRETS_FILE"
  sudo touch "$SECRETS_FILE"

  echo "Enter Telegram token:"
  read -r -s MY_INBOX_TELEGRAM_BOT_TOKEN
  echo ""

  echo "Enter chat id for Telegram reporting:"
  read -r -s MY_INBOX_TELEGRAM_BOT_CHAT_ID
  echo ""

  echo "Enter DeepL api key for translations:"
  read -r -s DEEPL_API_KEY
  echo ""

  printf "MY_INBOX_TELEGRAM_BOT_TOKEN=%s\n" "$MY_INBOX_TELEGRAM_BOT_TOKEN" | sudo tee -a "$SECRETS_FILE" > /dev/null
  printf "MY_INBOX_TELEGRAM_BOT_CHAT_ID=%s\n" "$MY_INBOX_TELEGRAM_BOT_CHAT_ID" | sudo tee -a "$SECRETS_FILE" > /dev/null
	printf "DEEPL_API_KEY=%s\n" "$DEEPL_API_KEY" | sudo tee -a "$SECRETS_FILE" > /dev/null
fi

sudo chown root:root "$SECRETS_FILE"
sudo chmod 600 "$SECRETS_FILE"

# Build JAR
(cd "$BOT_DIR" && ./gradlew :app:shadowJar)
JAR_FILE=$(find "$BOT_DIR/app/build/libs/" -name "*-all.jar" | head -n 1)

# Service

if [[ ! -f "$SERVICE_FILE" ]]; then
  echo "Creating service file: $SERVICE_FILE"
  cat << EOF | sudo tee "$SERVICE_FILE" > /dev/null
[Unit]
Description=My Inbox Telegram Bot
After=network-online.target

[Service]
Type=simple
User=$(whoami)
WorkingDirectory=$BOT_DIR
ExecStart=$JAVA_EXEC_PATH -jar "$JAR_FILE"
Restart=on-failure
RestartSec=5
StandardOutput=journal
StandardError=journal
EnvironmentFile=$SECRETS_FILE

[Install]
WantedBy=multi-user.target
EOF
fi

sudo systemctl daemon-reload
sudo systemctl enable my-inbox-telegram-bot.service
sudo systemctl start my-inbox-telegram-bot.service

echo "Bot setup complete"