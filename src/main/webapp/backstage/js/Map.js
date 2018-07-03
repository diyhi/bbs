function Map(linkItems) { 
    this.current = undefined; 
    this._size = 0; 
    if(linkItems === false){
    	this.disableLinking(); 
    } 
}
/**
 * 获取当前map
 * @return 当前对象
 */
Map.noop = function() { 
    return this; 
}; 
/**
 * 非法操作
 * @return
 */
Map.illegal = function() { 
    throw new Error("非法操作，Map已经被禁用"); 
}; 
/**
 * 
 * @param obj
 * @param foreignKeys
 * @return
 */
Map.from = function(obj, foreignKeys) { 
    var map = new Map; 
    for(var prop in obj) { 
        if(foreignKeys || obj.hasOwnProperty(prop)){
        	map.put(prop, obj[prop]); 
        } 
    } 
    return map; 
}; 
/**
 * 禁用map
 * @return
 */
Map.prototype.disableLinking = function() { 
    this.link = Map.noop; 
    this.unlink = Map.noop; 
    this.disableLinking = Map.noop; 
    this.next = Map.illegal; 
    this.key = Map.illegal; 
    this.value = Map.illegal; 
    this.clear = Map.illegal; 
    return this; 
}; 
/**
 * 返回hash值 例如：number 123
 * @param value key/value
 * @return
 */
Map.prototype.hash = function(value) { 
    return (typeof value) + ' ' + (value instanceof Object ? (value.__hash || (value.__hash = ++arguments.callee.current)) : value.toString()); 
}; 
/**
 * 返回map的长度
 * @return
 */
Map.prototype.size = function() { 
    return this._size;
}; 

Map.prototype.hash.current = 0; 
/**
 * 通过key获取value
 * @param key
 * @return
 */
Map.prototype.get = function(key) { 
    var item = this[this.hash(key)]; 
    return item === undefined ? undefined : item.value; 
}; 
/**
 * 向map中添加数据
 * @param key 键
 * @param value 值
 * @return
 */
Map.prototype.put = function(key, value) { 
    var hash = this.hash(key); 
    if(this[hash] === undefined) { 
        var item = { key : key, value : value }; 
        this[hash] = item; 
        this.link(item); 
        ++this._size; 
    }else{
    	this[hash].value = value;
    } 
    return this; 
}; 
/**
 * 通过key删除数据
 * @param key
 * @return
 */
Map.prototype.remove = function(key) { 
    var hash = this.hash(key); 
    var item = this[hash]; 
    if(item !== undefined) { 
        --this._size; 
        this.unlink(item); 
        delete this[hash]; 
    } 
    return this; 
}; 
/**
 * 清除map
 * @return
 */
Map.prototype.clear = function() { 
    while(this._size){
		this.remove(this.key()); 
	} 
    return this; 
}; 
/**
 * 处理队列
 * @param item
 * @return
 */
Map.prototype.link = function(item) { 
    if(this._size == 0) { 
        item.prev = item; 
        item.next = item; 
        this.current = item; 
    }else { 
        item.prev = this.current.prev; 
        item.prev.next = item; 
        item.next = this.current; 
        this.current.prev = item;
    } 
}; 
Map.prototype.unlink = function(item) { 
    if(this._size == 0){ 
        this.current = undefined;
    }else { 
        item.prev.next = item.next; 
        item.next.prev = item.prev; 
        if(item === this.current){
        	this.current = item.next; 
        } 
    } 
}; 
/**
 * 获取下一个
 * @return
 */
Map.prototype.next = function() { 
    this.current = this.current.next; 
    return this;
}; 
/**
 * 获取key
 * @return
 */
Map.prototype.key = function() { 
    return this.current.key; 
}; 
/**
 * 获取value
 * @return
 */
Map.prototype.value = function() { 
    return this.current.value; 
}; 