#!/usr/bin/env sh

export OAUTH_CLIENT_ID=${OAUTH_CLIENT_ID:-$1}
export OAUTH_CLIENT_SECRET=${OAUTH_CLIENT_SECRET:-$2}

SKIP_BUILD=${SKIP_BUILD:-false} 

if [[ -z $OAUTH_CLIENT_ID || -z $OAUTH_CLIENT_SECRET ]]; then
  echo "Usage $0 <oauth client id> <oauth client secret>"
  exit 1
fi

echo "
  ### SETTINGS ###

  ## BUILD ##
  skip build: $SKIP_BUILD

  ## OAUTH ##
  client id: $OAUTH_CLIENT_ID
  secret: $OAUTH_CLIENT_SECRET
"
