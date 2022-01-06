#!/usr/bin/env bash

   for path in $(pwd)/*; do
        name=$(basename "$path")
        if [[ "$name" == "Xcode_"* ]]; then
          echo "$name"
        fi
    done

