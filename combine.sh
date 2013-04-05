#!/bin/bash

cd pages
ls | grep "^[0-9]\+\.odt" | xargs -L 1 unoconv -f pdf
ls | grep "^[0-9]\+\.pdf" | xargs | xargs -I files echo "pdftk files cat output ../$1" | bash
ls | grep "^[0-9]\+\.odt" | xargs rm
ls | grep "^[0-9]\+\.pdf" | xargs rm
cd ..
rm -R pages
