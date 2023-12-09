#!/bin/bash

CURTAG=`git describe --abbrev=0 --tags`;
CURTAG="${CURTAG/v/}"
echo "... $CURTAG"
if [ -z ${CURTAG} ]; then 
	echo "No tags vX.Y.Z exists...";
	CURTAG="0.0.0"
fi

IFS='.' read -a vers <<< "$CURTAG"

MAJ=${vers[0]}
MIN=${vers[1]}
BUG=${vers[2]}
echo "Current last Tag: v$MAJ.$MIN.$BUG"

read -n1 -p "Choose what kind of TAG to create (m-major/i-minor/b-bug): " choice
echo ""
read -p "Provide a tag message: " MESSAGE_TAG
echo ""
case $choice in
		"m")
			# $((MAJ+1))
			((MAJ+=1))
			MIN=0
			BUG=0
			printf "Incrementing Major Version#"
			;;
		"i")
			((MIN+=1))
			BUG=0
			echo "Incrementing Minor Version#"
			;;
		"b")
			((BUG+=1))
			echo "Incrementing Bug Version#"
			;;
		* ) 
			echo "invalid"
			exit
			;;
	esac

NEWTAG="v$MAJ.$MIN.$BUG"
echo ""
echo "Adding Tag: $NEWTAG -> $MESSAGE_TAG" ;

# Ask to create and push
read -n1 -p "Continue (y/n)?" choice
echo ""
case "$choice" in 
	y|Y )
		git tag -a $NEWTAG -m "$MESSAGE_TAG"
		echo "Tag ok";
		read -n1 -p "Push tag? (y/n)" newchoice
		echo ""
		case "$newchoice" in 
			y|Y )
				git push origin $NEWTAG
      		;;
  			n|N ) echo "";;
		  	* ) echo "invalid";;
		esac
    	;;
  n|N ) echo "skip";;
  * ) echo "invalid";;
esac

echo "New list of tags:"
git tag 
echo "Done!"