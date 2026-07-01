local reactKey = KEYS[1]
local statsKey = KEYS[2]

local userId = ARGV[1]
local newReact = ARGV[2]
local emptyFlag = ARGV[3]
local ttl = tonumber(ARGV[4])

local currentReact = redis.call('HGET', reactKey, userId)

if not currentReact then
	redis.call('HSET', reactKey, userId, newReact)
	redis.call('HDEL', reactKey, emptyFlag)
	redis.call('EXPIRE', reactKey, ttl)

	redis.call('HINCRBY', statsKey, newReact, 1)
	redis.call('HINCRBY', statsKey, 'TOTAL', 1)
	redis.call('HDEL', statsKey, emptyFlag)
	redis.call('EXPIRE', statsKey, ttl)

	return 'CREATE'

elseif currentReact == newReact then
	redis.call('HDEL', reactKey, userId)

	local currentCount = redis.call('HINCRBY', statsKey, newReact, -1)
	local totalCount = redis.call('HINCRBY', statsKey, 'TOTAL', -1)

	if tonumber(currentCount) <= 0 then
		redis.call('HDEL', statsKey, newReact)
	end

	if tonumber(totalCount) <= 0 then
		redis.call('HDEL', statsKey, 'TOTAL')
	else
		redis.call('EXPIRE', statsKey, ttl)
	end

	return 'DELETE'

else
	redis.call('HSET', reactKey, userId, newReact)
	redis.call('EXPIRE', reactKey, ttl)

	redis.call('HINCRBY', statsKey, newReact, 1)
	local currentCount = redis.call('HINCRBY', statsKey, currentReact, -1)

	if tonumber(currentCount) <= 0 then
		redis.call('HDEL', statsKey, currentReact)
	end

	redis.call('EXPIRE', statsKey, ttl)

	return 'UPDATE'
end