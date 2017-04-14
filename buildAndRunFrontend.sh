#!/usr/bin/env sh

source ./common.sh

if [ -z "$CYGWIN" ]; then
  NPM_SCRIPT=startFancy
else
  NPM_SCRIPT=start
fi

sh -c "cd sam-front; npm install; npm run $NPM_SCRIPT"
