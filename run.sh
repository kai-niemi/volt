#!/bin/bash

set -e

case "$OSTYPE" in
  darwin*)
        default="\x1B[0m"
        cyan="\x1B[36m"
        lightblue="\x1B[94m"
        magenta="\x1B[35m"
        creeol="\r\033[K"
        ;;
  *)
        default="\e[0m"
        cyan="\e[36m"
        lightblue="\e[94m"
        magenta="\e[35m"
        creeol="\r\033[K"
        ;;
esac

fn_print_cyan(){
  echo -en "${creeol}${cyan}$@${default}"
	echo -en "\n"
}

fn_print_magenta(){
  echo -en "${creeol}${magenta}$@${default}"
	echo -en "\n"
}

fn_print_blue(){
  echo -en "${creeol}${lightblue}$@${default}"
	echo -en "\n"
}

basedir=.
jarfile=${basedir}/target/volt.jar
profiles=

if [ ! -f "$jarfile" ]; then
    ./mvnw clean install
fi

PS3='Please select application model: '
options=( "samples"/*.yml )

select option in "${options[@]}" "<Quit>";  do
  case $option in
    *.yml)
      profiles=$(echo $option | sed -e 's#^samples/application-##' -e "s/\.[^.]*$//")
      break
      ;;
    "<Quit>")
      exit 0
      ;;
    *)
      ;;
  esac
done

fn_print_cyan "Selected profiles: $profiles"

PS3='Please select additional profiles: '
options=( "proxy" "dev" "<Start>" "<Quit>" )

select option in "${options[@]}"; do
  case $option in
    "<Start>")
      break
      ;;
    "<Quit>")
      exit 0
      ;;
    *)
      profiles=$profiles,$option
      fn_print_cyan "Selected profiles: $profiles (press enter)"
      ;;
  esac
done

fn_print_blue java -jar ${jarfile} --profiles ${profiles} $*

java -jar ${jarfile} --profiles ${profiles} $*
