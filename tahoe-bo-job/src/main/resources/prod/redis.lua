local key = KEYS[1]
local listKey = KEYS[2]
local val =ARGV[1]

local listVal = {}
listVal[key]=key
listVal[val]=val

redis.call("SET",key,val)
redis.call("LPUSH",listKey,cjson.encode(listVal))



return result