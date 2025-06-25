#!/bin/bash

# Script to update the running MyInbox Telegram bot

BOT_DIR="$HOME/code/my-inbox-telegram-bot"
MYINBOX_SERVICE="my-inbox-telegram-bot.service"

git -C "$BOT_DIR" pull origin main || exit 1
sudo systemctl daemon-reload
(cd "$BOT_DIR" && ./gradlew clean :app:shadowJar) || exit 1
sudo systemctl restart "$MYINBOX_SERVICE"

echo "Bot update complete"