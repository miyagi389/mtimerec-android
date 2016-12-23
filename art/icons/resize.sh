#!/bin/bash

INK=/Applications/Inkscape.app/Contents/Resources/bin/inkscape

if [[ -z "$1" ]]
then
    echo "SVG file needed."
    exit;
fi

BASE=`basename "$1" .svg`
SVG=$(cd $(dirname $1) && pwd)/$1

OUTDIR=$(cd $(dirname $1) && pwd)/out

echo "BASE: " $BASE
echo "SVG: " $SVG
echo "OUTDIR: " $OUTDIR

if [ ! -d $OUTDIR ]; then
    mkdir -p $OUTDIR
fi


# Android
mkdir $OUTDIR/mipmap-mdpi
$INK -z -D -e "$OUTDIR/mipmap-mdpi/ic_launcher.png" -f $SVG -w 48 -h 48
mkdir $OUTDIR/mipmap-hdpi
$INK -z -D -e "$OUTDIR/mipmap-hdpi/ic_launcher.png" -f $SVG -w 72 -h 72
mkdir $OUTDIR/mipmap-xhdpi
$INK -z -D -e "$OUTDIR/mipmap-xhdpi/ic_launcher.png" -f $SVG -w 96 -h 96
mkdir $OUTDIR/mipmap-xxhdpi
$INK -z -D -e "$OUTDIR/mipmap-xxhdpi/ic_launcher.png" -f $SVG -w 144 -h 144
mkdir $OUTDIR/mipmap-xxxhdpi
$INK -z -D -e "$OUTDIR/mipmap-xxxhdpi/ic_launcher.png" -f $SVG -w 192 -h 192
$INK -z -D -e "$OUTDIR/ic_launcher-web.png" -f $SVG -w 512 -h 512


# iPhone Spotlight iOS5,6 Settings iOS and iPad 5-7 29pt
$INK -z -D -e "$OUTDIR/$BASE-29.png" -f     $SVG -w 29 -h 29
$INK -z -D -e "$OUTDIR/$BASE-29@2x.png" -f  $SVG -w 58 -h 58
$INK -z -D -e "$OUTDIR/$BASE-29@3x.png" -f  $SVG -w 87 -h 87

# iPhone Spotlight iOS7 40pt
$INK -z -D -e "$OUTDIR/$BASE-40@2x.png" -f  $SVG -w 80 -h 80
$INK -z -D -e "$OUTDIR/$BASE-40@3x.png" -f  $SVG -w 120 -h 120

# iPhone App iOS 5,6 57pt
# $INK -z -D -e "$OUTDIR/$BASE-57.png" -f   $SVG -w 57 -h 57
# $INK -z -D -e "$OUTDIR/$BASE-57@2x.png" -f    $SVG -w 114 -h 114

# iPhone App iOS 7 60pt
$INK -z -D -e "$OUTDIR/$BASE-60@2x.png" -f  $SVG -w 120 -h 120
$INK -z -D -e "$OUTDIR/$BASE-60@3x.png" -f  $SVG -w 180 -h 180

# iPad Spotlight iOS 7 40pt
$INK -z -D -e "$OUTDIR/$BASE-40.png" -f     $SVG -w 40 -h 40

# iPad Spotlight iOS 5,6 50pt
# $INK -z -D -e "$OUTDIR/$BASE-50.png" -f   $SVG -w 50 -h 50
# $INK -z -D -e "$OUTDIR/$BASE-50@2x.png" -f    $SVG -w 100 -h 100

# iPad App iOS 5,6 72pt
# $INK -z -D -e "$OUTDIR/$BASE-72.png" -f   $SVG -w 72 -h 72
# $INK -z -D -e "$OUTDIR/$BASE-72@2x.png" -f    $SVG -w 144 -h 144

# iPad App iOS 7  76pt
$INK -z -D -e "$OUTDIR/$BASE-76.png" -f     $SVG -w 76 -h 76
$INK -z -D -e "$OUTDIR/$BASE-76@2x.png" -f  $SVG -w 152 -h 152

#iTunes Artwork
$INK -z -D -e "$OUTDIR/$BASE-512.png" -f    $SVG -w 512 -h 512
$INK -z -D -e "$OUTDIR/$BASE-1024.png" -f   $SVG -w 1024 -h 1024

cp "$OUTDIR/$BASE-512.png" $OUTDIR/iTunesArtwork.png
cp "$OUTDIR/$BASE-1024.png" $OUTDIR/iTunesArtwork@2x.png
