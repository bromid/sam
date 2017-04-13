#!/usr/bin/env sh

# User configuration
export OAUTH_CLIENT_ID=${OAUTH_CLIENT_ID:-$1}
export OAUTH_CLIENT_SECRET=${OAUTH_CLIENT_SECRET:-$2}

if [[ -z $OAUTH_CLIENT_ID || -z $OAUTH_CLIENT_SECRET ]]; then
  echo "Usage $0 <oauth client id> <oauth client secret>"
  exit 1
fi

# Default configuration for oauth against github.com
export OAUTH_SCOPES=${OAUTH_SCOPES:-"user"}
export OAUTH_AUTHORIZE_ENDPOINT=${OAUTH_AUTHORIZE_ENDPOINT:-"https://github.com/login/oauth/authorize"}
export OAUTH_ACCESSTOKEN_ENDPOINT=${OAUTH_ACCESSTOKEN_ENDPOINT:-"https://github.com/login/oauth/access_token"}
export OAUTH_GITHUB_USER_ENDPOINT=${OAUTH_GITHUB_USER_ENDPOINT:-"https://api.github.com/user"}

export IDTOKEN_PUBLICKEY_EXPONENT=${IDTOKEN_PUBLICKEY_EXPONENT:-"NA"}
export IDTOKEN_PUBLICKEY_MODULUS=${IDTOKEN_PUBLICKEY_MODULUS:-"NA"}
export IDTOKEN_ISSUER=${IDTOKEN_ISSUER:-"https://sam.atg.se"}
export IDTOKEN_AUDIENCE=${IDTOKEN_AUDIENCE:-"https://sam.atg.se"}

SKIP_BUILD=${SKIP_BUILD:-false} 

echo "
  ### SETTINGS ###

  ## BUILD ##
  Skip build: $SKIP_BUILD

  ## OAUTH ##
  Client id: $OAUTH_CLIENT_ID
  Secret: $OAUTH_CLIENT_SECRET
  Scopes: $OAUTH_SCOPES
  Authorize endpoint: $OAUTH_AUTHORIZE_ENDPOINT
  Access token endpoint: $OAUTH_ACCESSTOKEN_ENDPOINT
  Github user endpoint: $OAUTH_GITHUB_USER_ENDPOINT

  ## ID TOKEN ##
  Public key exponent: $IDTOKEN_PUBLICKEY_EXPONENT
  Public key modulues: $IDTOKEN_PUBLICKEY_MODULUS
  Issuer: $IDTOKEN_ISSUER
  Audience: $IDTOKEN_AUDIENCE
"
