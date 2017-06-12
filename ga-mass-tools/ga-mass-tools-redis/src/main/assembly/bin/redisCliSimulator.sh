#!/usr/bin/env bash

sum=0;
count=0;
for mkey in `redis-cli keys m_*`; do
    pos=0;
    hashKey="";
    for entry in `redis-cli hgetall $mkey`; do
        b=$(($pos % 2))
        if [ $b -eq 0 ]; then
            hashKey=$entry
        elif [ $b -eq 1 ]; then
            oldIFS=$IFS
            IFS="|"
            strArr=($entry);
            c=${strArr[2]}
            if [ $c -lt 1483891200 -a $c -gt 1483851200 ]; then
                redis-cli hdel $mkey $hashKey
                count=$(($count + 1));
            fi
            sum=$(($sum + 1));
            IFS=$oldIFS
        fi
        pos=$(($pos + 1));
    done;
done;
echo "the delete count is $count"
echo "the sum is $sum"