#!/bin/bash

for i in `git status -s`;
do
  if [ $i != "D" ]
  then
    git rm $i
  fi
done

