#!/usr/bin/env bash

if [[ $OSTYPE != 'darwin'* ]]; then
  echo "This script requires MAC OS"
  exit 0
fi

: "${ESEWA_BUILD_DIR:=}"

set -euo pipefail

get_swift_version() {
    swift -version 2>/dev/null | sed -ne 's/^Apple Swift version \([^\b ]*\).*/\1/p'
}

get_xcode_version() {
    xcodebuild -version 2>/dev/null | sed -ne 's/^Xcode \([^\b ]*\).*/\1/p'
}

is_xcode_version() {
  test "$(get_xcode_version)" = "$1"
}

is_swift_version() {
  test "$(get_swift_version)" = "$1*"
}

current_dir() {
  cd -P "$(dirname "$(readlink "${BASH_SOURCE[0]}" || echo "${BASH_SOURCE[0]}")")" && pwd
}


get_ios_build_path() {
  requiredBinaryName="Xcode_$(get_xcode_version)-Swift_$(get_swift_version)"
  echo "required: $requiredBinaryName"

  : "${exact_binary_path:=}"
  : "${lazy_binary_path:=}"

  for path in $1; do
    name=$(basename "$path")

    if [[ -z "$exact_binary_path" ]]; then
          if [[ "$name" == "Xcode_"* ]]; then
            if [[ "$requiredBinaryName" == "$name" ]]; then
                exact_binary_path=$path
            elif [[ "$requiredBinaryName" == "$name"* ]]; then
                lazy_binary_path=$path
            fi
          fi
    fi
  done

  if [[ -n "$exact_binary_path" ]]; then
      ESEWA_BUILD_DIR="$exact_binary_path"
      return 0
  elif [[ -n "$lazy_binary_path" ]]; then
      ESEWA_BUILD_DIR="$lazy_binary_path"
      return 0
  fi
}

# Select build
get_ios_build_path "$(current_dir)/../apple/releases/*"

pushd "$(current_dir)/../apple/"

echo "RESULT: $ESEWA_BUILD_DIR"

# Cleanup
rm -rf EsewaSDK.xcframework


# Copy
cp -R "$ESEWA_BUILD_DIR" EsewaSDK.xcframework
echo "Done!"

popd
