(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
	typeof define === 'function' && define.amd ? define(factory) :
	(global.wangEditor = factory());
}(this, (function () { 'use strict';

/*
 * v3.1.1
    poly-fill
*/

var polyfill = function () {

    // Object.assign
    if (typeof Object.assign != 'function') {
        Object.assign = function (target, varArgs) {
            // .length of function is 2
            if (target == null) {
                // TypeError if undefined or null
                throw new TypeError('Cannot convert undefined or null to object');
            }

            var to = Object(target);

            for (var index = 1; index < arguments.length; index++) {
                var nextSource = arguments[index];

                if (nextSource != null) {
                    // Skip over if undefined or null
                    for (var nextKey in nextSource) {
                        // Avoid bugs when hasOwnProperty is shadowed
                        if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
                            to[nextKey] = nextSource[nextKey];
                        }
                    }
                }
            }
            return to;
        };
    }

    // IE 中兼容 Element.prototype.matches
    if (!Element.prototype.matches) {
        Element.prototype.matches = Element.prototype.matchesSelector || Element.prototype.mozMatchesSelector || Element.prototype.msMatchesSelector || Element.prototype.oMatchesSelector || Element.prototype.webkitMatchesSelector || function (s) {
            var matches = (this.document || this.ownerDocument).querySelectorAll(s),
                i = matches.length;
            while (--i >= 0 && matches.item(i) !== this) {}
            return i > -1;
        };
    }
};

/*
    DOM 操作 API
*/

// 根据 html 代码片段创建 dom 对象
function createElemByHTML(html) {
    var div = void 0;
    div = document.createElement('div');
    div.innerHTML = html;
    return div.children;
}

// 是否是 DOM List
function isDOMList(selector) {
    if (!selector) {
        return false;
    }
    if (selector instanceof HTMLCollection || selector instanceof NodeList) {
        return true;
    }
    return false;
}

// 封装 document.querySelectorAll
function querySelectorAll(selector) {
    var result = document.querySelectorAll(selector);
    if (isDOMList(result)) {
        return result;
    } else {
        return [result];
    }
}

// 记录所有的事件绑定
var eventList = [];

// 创建构造函数
function DomElement(selector) {
    if (!selector) {
        return;
    }

    // selector 本来就是 DomElement 对象，直接返回
    if (selector instanceof DomElement) {
        return selector;
    }

    this.selector = selector;
    var nodeType = selector.nodeType;

    // 根据 selector 得出的结果（如 DOM，DOM List）
    var selectorResult = [];
    if (nodeType === 9) {
        // document 节点
        selectorResult = [selector];
    } else if (nodeType === 1) {
        // 单个 DOM 节点
        selectorResult = [selector];
    } else if (isDOMList(selector) || selector instanceof Array) {
        // DOM List 或者数组
        selectorResult = selector;
    } else if (typeof selector === 'string') {
        // 字符串
        selector = selector.replace('/\n/mg', '').trim();
        if (selector.indexOf('<') === 0) {
            // 如 <div>
            selectorResult = createElemByHTML(selector);
        } else {
            // 如 #id .class
            selectorResult = querySelectorAll(selector);
        }
    }

    var length = selectorResult.length;
    if (!length) {
        // 空数组
        return this;
    }

    // 加入 DOM 节点
    var i = void 0;
    for (i = 0; i < length; i++) {
        this[i] = selectorResult[i];
    }
    this.length = length;
}

// 修改原型
DomElement.prototype = {
    constructor: DomElement,

    // 类数组，forEach
    forEach: function forEach(fn) {
        var i = void 0;
        for (i = 0; i < this.length; i++) {
            var elem = this[i];
            var result = fn.call(elem, elem, i);
            if (result === false) {
                break;
            }
        }
        return this;
    },

    // clone
    clone: function clone(deep) {
        var cloneList = [];
        this.forEach(function (elem) {
            cloneList.push(elem.cloneNode(!!deep));
        });
        return $(cloneList);
    },

    // 获取第几个元素
    get: function get(index) {
        var length = this.length;
        if (index >= length) {
            index = index % length;
        }
        return $(this[index]);
    },

    // 第一个
    first: function first() {
        return this.get(0);
    },

    // 最后一个
    last: function last() {
        var length = this.length;
        return this.get(length - 1);
    },

    // 绑定事件
    on: function on(type, selector, fn) {
        // selector 不为空，证明绑定事件要加代理
        if (!fn) {
            fn = selector;
            selector = null;
        }

        // type 是否有多个
        var types = [];
        types = type.split(/\s+/);

        return this.forEach(function (elem) {
            types.forEach(function (type) {
                if (!type) {
                    return;
                }

                // 记录下，方便后面解绑
                eventList.push({
                    elem: elem,
                    type: type,
                    fn: fn
                });

                if (!selector) {
                    // 无代理
                    elem.addEventListener(type, fn);
                    return;
                }

                // 有代理
                elem.addEventListener(type, function (e) {
                    var target = e.target;
                    if (target.matches(selector)) {
                        fn.call(target, e);
                    }
                });
            });
        });
    },

    // 取消事件绑定
    off: function off(type, fn) {
        return this.forEach(function (elem) {
            elem.removeEventListener(type, fn);
        });
    },

    // 获取/设置 属性
    attr: function attr(key, val) {
        if (val == null) {
            // 获取值
            return this[0].getAttribute(key);
        } else {
            // 设置值
            return this.forEach(function (elem) {
                elem.setAttribute(key, val);
            });
        }
    },

    // 添加 class
    addClass: function addClass(className) {
        if (!className) {
            return this;
        }
        return this.forEach(function (elem) {
            var arr = void 0;
            if (elem.className) {
                // 解析当前 className 转换为数组
                arr = elem.className.split(/\s/);
                arr = arr.filter(function (item) {
                    return !!item.trim();
                });
                // 添加 class
                if (arr.indexOf(className) < 0) {
                    arr.push(className);
                }
                // 修改 elem.class
                elem.className = arr.join(' ');
            } else {
                elem.className = className;
            }
        });
    },

    // 删除 class
    removeClass: function removeClass(className) {
        if (!className) {
            return this;
        }
        return this.forEach(function (elem) {
            var arr = void 0;
            if (elem.className) {
                // 解析当前 className 转换为数组
                arr = elem.className.split(/\s/);
                arr = arr.filter(function (item) {
                    item = item.trim();
                    // 删除 class
                    if (!item || item === className) {
                        return false;
                    }
                    return true;
                });
                // 修改 elem.class
                elem.className = arr.join(' ');
            }
        });
    },

    // 修改 css
    css: function css(key, val) {
        var currentStyle = key + ':' + val + ';';
        return this.forEach(function (elem) {
            var style = (elem.getAttribute('style') || '').trim();
            var styleArr = void 0,
                resultArr = [];
            if (style) {
                // 将 style 按照 ; 拆分为数组
                styleArr = style.split(';');
                styleArr.forEach(function (item) {
                    // 对每项样式，按照 : 拆分为 key 和 value
                    var arr = item.split(':').map(function (i) {
                        return i.trim();
                    });
                    if (arr.length === 2) {
                        resultArr.push(arr[0] + ':' + arr[1]);
                    }
                });
                // 替换或者新增
                resultArr = resultArr.map(function (item) {
                    if (item.indexOf(key) === 0) {
                        return currentStyle;
                    } else {
                        return item;
                    }
                });
                if (resultArr.indexOf(currentStyle) < 0) {
                    resultArr.push(currentStyle);
                }
                // 结果
                elem.setAttribute('style', resultArr.join('; '));
            } else {
                // style 无值
                elem.setAttribute('style', currentStyle);
            }
        });
    },

    // 显示
    show: function show() {
        return this.css('display', 'block');
    },

    // 隐藏
    hide: function hide() {
        return this.css('display', 'none');
    },

    // 获取子节点
    children: function children() {
        var elem = this[0];
        if (!elem) {
            return null;
        }

        return $(elem.children);
    },

    // 获取子节点（包括文本节点）
    childNodes: function childNodes() {
        var elem = this[0];
        if (!elem) {
            return null;
        }

        return $(elem.childNodes);
    },

    // 增加子节点
    append: function append($children) {
        return this.forEach(function (elem) {
            $children.forEach(function (child) {
                elem.appendChild(child);
            });
        });
    },

    // 移除当前节点
    remove: function remove() {
        return this.forEach(function (elem) {
            if (elem.remove) {
                elem.remove();
            } else {
                var parent = elem.parentElement;
                parent && parent.removeChild(elem);
            }
        });
    },

    // 是否包含某个子节点
    isContain: function isContain($child) {
        var elem = this[0];
        var child = $child[0];
        return elem.contains(child);
    },

    // 尺寸数据
    getSizeData: function getSizeData() {
        var elem = this[0];
        return elem.getBoundingClientRect(); // 可得到 bottom height left right top width 的数据
    },

    // 封装 nodeName
    getNodeName: function getNodeName() {
        var elem = this[0];
        return elem.nodeName;
    },

    // 从当前元素查找
    find: function find(selector) {
        var elem = this[0];
        return $(elem.querySelectorAll(selector));
    },

    // 获取当前元素的 text
    text: function text(val) {
        if (!val) {
            // 获取 text
            var elem = this[0];
            return elem.innerHTML.replace(/<.*?>/g, function () {
                return '';
            });
        } else {
            // 设置 text
            return this.forEach(function (elem) {
                elem.innerHTML = val;
            });
        }
    },

    // 获取 html
    html: function html(value) {
        var elem = this[0];
        if (value == null) {
        	 //图片标签转视频标签
        	this.imgToVideo();

        	var html = elem.innerHTML;
        	
        	//视频标签转图片标签
            this.videoToImg();
        	
            return html;
        } else {
            elem.innerHTML = value;
            
            //视频标签转图片标签
            this.videoToImg();
            
            return this;
        }
    },
    // 视频标签转图片标签
    videoToImg: function videoToImg() {
    	//将所有video标签加上preload ="none"属性，让浏览器不加载视频文件
        //this.find("video").attr("preload","none");
        
        //将所有的视频标签转为指定图片标签
        this.find("video").forEach(function (item) {
        	//获取Javascript文件自身所在URL路径
        	var scripts = document.scripts;
        	var url =scripts[scripts.length - 1].src;
        	url = url.substring(0, url.lastIndexOf('/'));

        	var node = $('<img class="playerImg" src="'+url+'/wangeditor/play.png" data="'+$(item).attr("src")+'"/>');
        	node.insertAfter(item);
        	$(item).remove();//删除当前节点
        });
    },
    // 图片标签转视频标签
    imgToVideo: function imgToVideo() {
    	//将所有的视频转图片类型的标签转为视频标签
        this.find("img").forEach(function (item) {
        	
        	if($(item).attr("class") == "playerImg"){
        		//获取Javascript文件自身所在URL路径
            	var scripts = document.scripts;
            	var url =scripts[scripts.length - 1].src;
            	url = url.substring(0, url.lastIndexOf('/'));
            	var node = $('<video width="50px" height="50px" src="'+$(item).attr("data")+'" controls="controls" preload="none"/>');
            	node.insertAfter(item);
            	$(item).remove();//删除当前节点
        	}
        });
    },
    // 获取 value
    val: function val() {
        var elem = this[0];
        return elem.value.trim();
    },

    // focus
    focus: function focus() {
        return this.forEach(function (elem) {
            elem.focus();
        });
    },

    // parent
    parent: function parent() {
        var elem = this[0];
        return $(elem.parentElement);
    },

    // parentUntil 找到符合 selector 的父节点
    parentUntil: function parentUntil(selector, _currentElem) {
        var results = document.querySelectorAll(selector);
        var length = results.length;
        if (!length) {
            // 传入的 selector 无效
            return null;
        }

        var elem = _currentElem || this[0];
        if (elem.nodeName === 'BODY') {
            return null;
        }
        var parent = elem.parentElement;
        var i = void 0;
        for (i = 0; i < length; i++) {
            if (parent === results[i]) {
                // 找到，并返回
                return $(parent);
            }
        }

        // 继续查找
        return this.parentUntil(selector, parent);
    },

    // 判断两个 elem 是否相等
    equal: function equal($elem) {
        if ($elem.nodeType === 1) {
            return this[0] === $elem;
        } else {
            return this[0] === $elem[0];
        }
    },

    // 将该元素插入到某个元素前面
    insertBefore: function insertBefore(selector) {
        var $referenceNode = $(selector);
        var referenceNode = $referenceNode[0];
        if (!referenceNode) {
            return this;
        }
        return this.forEach(function (elem) {
            var parent = referenceNode.parentNode;
            parent.insertBefore(elem, referenceNode);
        });
    },

    // 将该元素插入到某个元素后面
    insertAfter: function insertAfter(selector) {
        var $referenceNode = $(selector);
        var referenceNode = $referenceNode[0];
        if (!referenceNode) {
            return this;
        }
        return this.forEach(function (elem) {
            var parent = referenceNode.parentNode;
            if (parent.lastChild === referenceNode) {
                // 最后一个元素
                parent.appendChild(elem);
            } else {
                // 不是最后一个元素
                parent.insertBefore(elem, referenceNode.nextSibling);
            }
        });
    }
};

// new 一个对象
function $(selector) {
    return new DomElement(selector);
}

// 解绑所有事件，用于销毁编辑器
$.offAll = function () {
    eventList.forEach(function (item) {
        var elem = item.elem;
        var type = item.type;
        var fn = item.fn;
        // 解绑
        elem.removeEventListener(type, fn);
    });
};

/*
    配置信息
*/

var config = {

    // 默认菜单配置
    menus: ['head', 'bold', 'fontSize', 'fontName', 'italic', 'underline', 'strikeThrough', 'foreColor', 'backColor', 'link', 'list', 'justify', 'quote', 'emoticon', 'image', 'table', 'video', 'code', 'undo', 'redo'],

    fontNames: ['宋体', '微软雅黑', 'Arial', 'Tahoma', 'Verdana'],

    colors: ['#000000', '#eeece0', '#1c487f', '#4d80bf', '#c24f4a', '#8baa4a', '#7b5ba1', '#46acc8', '#f9963b', '#ffffff'],

    // // 语言配置
    // lang: {
    //     '设置标题': 'title',
    //     '正文': 'p',
    //     '链接文字': 'link text',
    //     '链接': 'link',
    //     '插入': 'insert',
    //     '创建': 'init'
    // },

    // 表情
    emotions: [{
        // tab 的标题
        title: '默认',
        // type -> 'emoji' / 'image'
        type: 'image',
        // content -> 数组
        content: [{
            alt: '[坏笑]',
            src: 'http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/50/pcmoren_huaixiao_org.png'
        }, {
            alt: '[舔屏]',
            src: 'http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/40/pcmoren_tian_org.png'
        }, {
            alt: '[污]',
            src: 'http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/3c/pcmoren_wu_org.png'
        }]
    }, {
        // tab 的标题
        title: '新浪',
        // type -> 'emoji' / 'image'
        type: 'image',
        // content -> 数组
        content: [{
            src: 'http://img.t.sinajs.cn/t35/style/images/common/face/ext/normal/7a/shenshou_thumb.gif',
            alt: '[草泥马]'
        }, {
            src: 'http://img.t.sinajs.cn/t35/style/images/common/face/ext/normal/60/horse2_thumb.gif',
            alt: '[神马]'
        }, {
            src: 'http://img.t.sinajs.cn/t35/style/images/common/face/ext/normal/bc/fuyun_thumb.gif',
            alt: '[浮云]'
        }]
    }, {
        // tab 的标题
        title: 'emoji',
        // type -> 'emoji' / 'image'
        type: 'emoji',
        // content -> 数组
        content: '😀 😃 😄 😁 😆 😅 😂 😊 😇 🙂 🙃 😉 😓 😪 😴 🙄 🤔 😬 🤐'.split(/\s/)
    }],

    // 编辑区域的 z-index
    zIndex: 10000,

    // 是否开启 debug 模式（debug 模式下错误会 throw error 形式抛出）
    debug: false,

    // 插入链接时候的格式校验
    linkCheck: function linkCheck(text, link) {
        // text 是插入的文字
        // link 是插入的链接
        return true; // 返回 true 即表示成功
        // return '校验失败' // 返回字符串即表示失败的提示信息
    },

    // 插入网络图片的校验
    linkImgCheck: function linkImgCheck(src) {
        // src 即图片的地址
        return true; // 返回 true 即表示成功
        // return '校验失败'  // 返回字符串即表示失败的提示信息
    },

    // 粘贴过滤样式，默认开启
    pasteFilterStyle: true,

    // 粘贴内容时，忽略图片。默认关闭
    pasteIgnoreImg: false,

    // 对粘贴的文字进行自定义处理，返回处理后的结果。编辑器会将处理后的结果粘贴到编辑区域中。
    // IE 暂时不支持
    pasteTextHandle: function pasteTextHandle(content) {
        // content 即粘贴过来的内容（html 或 纯文本），可进行自定义处理然后返回
        return content;
    },

    // onchange 事件
    // onchange: function (html) {
    //     // html 即变化之后的内容
    //     console.log(html)
    // },

    // 是否显示添加网络图片的 tab
    showLinkImg: true,

    // 插入网络图片的回调
    linkImgCallback: function linkImgCallback(url) {
        // console.log(url)  // url 即插入图片的地址
    },

    // 默认上传图片 max size: 5M
    uploadImgMaxSize: 5 * 1024 * 1024,

    // 配置一次最多上传几个图片
    // uploadImgMaxLength: 5,

    // 上传图片，是否显示 base64 格式
    uploadImgShowBase64: false,

    // 上传图片，server 地址（如果有值，则 base64 格式的配置则失效）
    // uploadImgServer: '/upload',

    // 自定义配置 filename
    uploadFileName: '',

    // 上传图片的自定义参数
    uploadImgParams: {
        // token: 'abcdef12345'
    },

    // 上传图片的自定义header
    uploadImgHeaders: {
        // 'Accept': 'text/x-json'
    },

    // 配置 XHR withCredentials
    withCredentials: false,

    // 自定义上传图片超时时间 ms
    uploadImgTimeout: 10000,

    // 上传图片 hook 
    uploadImgHooks: {
        // customInsert: function (insertLinkImg, result, editor) {
        //     console.log('customInsert')
        //     // 图片上传并返回结果，自定义插入图片的事件，而不是编辑器自动插入图片
        //     const data = result.data1 || []
        //     data.forEach(link => {
        //         insertLinkImg(link)
        //     })
        // },
        before: function before(xhr, editor, files) {
            // 图片上传之前触发

            // 如果返回的结果是 {prevent: true, msg: 'xxxx'} 则表示用户放弃上传
            // return {
            //     prevent: true,
            //     msg: '放弃上传'
            // }
        },
        success: function success(xhr, editor, result) {
            // 图片上传并返回结果，图片插入成功之后触发
        },
        fail: function fail(xhr, editor, result) {
            // 图片上传并返回结果，但图片插入错误时触发
        },
        error: function error(xhr, editor) {
            // 图片上传出错时触发
        },
        timeout: function timeout(xhr, editor) {
            // 图片上传超时时触发
        }
    },

    // 是否上传七牛云，默认为 false
    qiniu: false

};

/*
    工具
*/

// 和 UA 相关的属性
var UA = {
    _ua: navigator.userAgent,

    // 是否 webkit
    isWebkit: function isWebkit() {
        var reg = /webkit/i;
        return reg.test(this._ua);
    },

    // 是否 IE
    isIE: function isIE() {
        return 'ActiveXObject' in window;
    }
};

// 遍历对象
function objForEach(obj, fn) {
    var key = void 0,
        result = void 0;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) {
            result = fn.call(obj, key, obj[key]);
            if (result === false) {
                break;
            }
        }
    }
}

// 遍历类数组
function arrForEach(fakeArr, fn) {
    var i = void 0,
        item = void 0,
        result = void 0;
    var length = fakeArr.length || 0;
    for (i = 0; i < length; i++) {
        item = fakeArr[i];
        result = fn.call(fakeArr, item, i);
        if (result === false) {
            break;
        }
    }
}

// 获取随机数
function getRandom(prefix) {
    return prefix + Math.random().toString().slice(2);
}

// 替换 html 特殊字符
function replaceHtmlSymbol(html) {
    if (html == null) {
        return '';
    }
    return html.replace(/</gm, '&lt;').replace(/>/gm, '&gt;').replace(/"/gm, '&quot;').replace(/(\r\n|\r|\n)/g, '<br/>');
}

// 返回百分比的格式


// 判断是不是 function
function isFunction(fn) {
    return typeof fn === 'function';
}

/*
    bold-menu
*/
// 构造函数
function Bold(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-bold"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Bold.prototype = {
    constructor: Bold,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;
        var isSeleEmpty = editor.selection.isSelectionEmpty();

        if (isSeleEmpty) {
            // 选区是空的，插入并选中一个“空白”
            editor.selection.createEmptyRange();
        }

        // 执行 bold 命令
        editor.cmd.do('bold');

        if (isSeleEmpty) {
            // 需要将选取折叠起来
            editor.selection.collapseRange();
            editor.selection.restoreSelection();
        }
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor.cmd.queryCommandState('bold')) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    替换多语言
 */

var replaceLang = function (editor, str) {
    var langArgs = editor.config.langArgs || [];
    var result = str;

    langArgs.forEach(function (item) {
        var reg = item.reg;
        var val = item.val;

        if (reg.test(result)) {
            result = result.replace(reg, function () {
                return val;
            });
        }
    });

    return result;
};

/*
    droplist
*/
var _emptyFn = function _emptyFn() {};

// 构造函数
function DropList(menu, opt) {
    var _this = this;

    // droplist 所依附的菜单
    var editor = menu.editor;
    this.menu = menu;
    this.opt = opt;
    // 容器
    var $container = $('<div class="w-e-droplist"></div>');

    // 标题
    var $title = opt.$title;
    var titleHtml = void 0;
    if ($title) {
        // 替换多语言
        titleHtml = $title.html();
        titleHtml = replaceLang(editor, titleHtml);
        $title.html(titleHtml);

        $title.addClass('w-e-dp-title');
        $container.append($title);
    }

    var list = opt.list || [];
    var type = opt.type || 'list'; // 'list' 列表形式（如“标题”菜单） / 'inline-block' 块状形式（如“颜色”菜单）
    var onClick = opt.onClick || _emptyFn;

    // 加入 DOM 并绑定事件
    var $list = $('<ul class="' + (type === 'list' ? 'w-e-list' : 'w-e-block') + '"></ul>');
    $container.append($list);
    list.forEach(function (item) {
        var $elem = item.$elem;

        // 替换多语言
        var elemHtml = $elem.html();
        elemHtml = replaceLang(editor, elemHtml);
        $elem.html(elemHtml);

        var value = item.value;
        var $li = $('<li class="w-e-item"></li>');
        if ($elem) {
            $li.append($elem);
            $list.append($li);
            $li.on('click', function (e) {
                onClick(value);

                // 隐藏
                _this.hideTimeoutId = setTimeout(function () {
                    _this.hide();
                }, 0);
            });
        }
    });

    // 绑定隐藏事件
    $container.on('mouseleave', function (e) {
        _this.hideTimeoutId = setTimeout(function () {
            _this.hide();
        }, 0);
    });

    // 记录属性
    this.$container = $container;

    // 基本属性
    this._rendered = false;
    this._show = false;
}

// 原型
DropList.prototype = {
    constructor: DropList,

    // 显示（插入DOM）
    show: function show() {
        if (this.hideTimeoutId) {
            // 清除之前的定时隐藏
            clearTimeout(this.hideTimeoutId);
        }

        var menu = this.menu;
        var $menuELem = menu.$elem;
        var $container = this.$container;
        if (this._show) {
            return;
        }
        if (this._rendered) {
            // 显示
            $container.show();
        } else {
            // 加入 DOM 之前先定位位置
            var menuHeight = $menuELem.getSizeData().height || 0;
            var width = this.opt.width || 100; // 默认为 100
            $container.css('margin-top', menuHeight + 'px').css('width', width + 'px');

            // 加入到 DOM
            $menuELem.append($container);
            this._rendered = true;
        }

        // 修改属性
        this._show = true;
    },

    // 隐藏（移除DOM）
    hide: function hide() {
        if (this.showTimeoutId) {
            // 清除之前的定时显示
            clearTimeout(this.showTimeoutId);
        }

        var $container = this.$container;
        if (!this._show) {
            return;
        }
        // 隐藏并需改属性
        $container.hide();
        this._show = false;
    }
};

/*
    menu - header
*/
// 构造函数
function Head(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-header"></i></div>');
    this.type = 'droplist';

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 100,
        $title: $('<p>设置标题</p>'),
        type: 'list', // droplist 以列表形式展示
        list: [{ $elem: $('<h1>H1</h1>'), value: '<h1>' }, { $elem: $('<h2>H2</h2>'), value: '<h2>' }, { $elem: $('<h3>H3</h3>'), value: '<h3>' }, { $elem: $('<h4>H4</h4>'), value: '<h4>' }, { $elem: $('<h5>H5</h5>'), value: '<h5>' }, { $elem: $('<p>正文</p>'), value: '<p>' }],
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 Head 对象
            _this._command(value);
        }
    });
}

// 原型
Head.prototype = {
    constructor: Head,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;

        var $selectionElem = editor.selection.getSelectionContainerElem();
        if (editor.$textElem.equal($selectionElem)) {
            // 不能选中多行来设置标题，否则会出现问题
            // 例如选中的是 <p>xxx</p><p>yyy</p> 来设置标题，设置之后会成为 <h1>xxx<br>yyy</h1> 不符合预期
            return;
        }

        editor.cmd.do('formatBlock', value);
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        var reg = /^h/i;
        var cmdValue = editor.cmd.queryCommandValue('formatBlock');
        if (reg.test(cmdValue)) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    menu - fontSize
*/

// 构造函数
function FontSize(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-text-heigh"></i></div>');
    this.type = 'droplist';

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 160,
        $title: $('<p>字号</p>'),
        type: 'list', // droplist 以列表形式展示
        list: [{ $elem: $('<span style="font-size: x-small;">x-small</span>'), value: '1' }, { $elem: $('<span style="font-size: small;">small</span>'), value: '2' }, { $elem: $('<span>normal</span>'), value: '3' }, { $elem: $('<span style="font-size: large;">large</span>'), value: '4' }, { $elem: $('<span style="font-size: x-large;">x-large</span>'), value: '5' }, { $elem: $('<span style="font-size: xx-large;">xx-large</span>'), value: '6' }],
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 FontSize 对象
            _this._command(value);
        }
    });
}

// 原型
FontSize.prototype = {
    constructor: FontSize,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;
        editor.cmd.do('fontSize', value);
    }
};

/*
    menu - fontName
*/

// 构造函数
function FontName(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-font"></i></div>');
    this.type = 'droplist';

    // 当前是否 active 状态
    this._active = false;

    // 获取配置的字体
    var config = editor.config;
    var fontNames = config.fontNames || [];

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 100,
        $title: $('<p>字体</p>'),
        type: 'list', // droplist 以列表形式展示
        list: fontNames.map(function (fontName) {
            return { $elem: $('<span style="font-family: ' + fontName + ';">' + fontName + '</span>'), value: fontName };
        }),
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 FontName 对象
            _this._command(value);
        }
    });
}

// 原型
FontName.prototype = {
    constructor: FontName,

    _command: function _command(value) {
        var editor = this.editor;
        editor.cmd.do('fontName', value);
    }
};

/*
    panel
*/

var emptyFn = function emptyFn() {};

// 记录已经显示 panel 的菜单
var _isCreatedPanelMenus = [];

// 构造函数
function Panel(menu, opt) {
    this.menu = menu;
    this.opt = opt;
}

// 原型
Panel.prototype = {
    constructor: Panel,

    // 显示（插入DOM）
    show: function show() {
        var _this = this;

        var menu = this.menu;
        if (_isCreatedPanelMenus.indexOf(menu) >= 0) {
            // 该菜单已经创建了 panel 不能再创建
            return;
        }

        var editor = menu.editor;
        var $body = $('body');
        var $textContainerElem = editor.$textContainerElem;
        var opt = this.opt;

        // panel 的容器
        var $container = $('<div class="w-e-panel-container"></div>');
        var width = opt.width || 300; // 默认 300px
        $container.css('width', width + 'px').css('margin-left', (0 - width) / 2 + 'px');

        // 添加关闭按钮
        var $closeBtn = $('<i class="w-e-icon-close w-e-panel-close"></i>');
        $container.append($closeBtn);
        $closeBtn.on('click', function () {
            _this.hide();
        });

        // 准备 tabs 容器
        var $tabTitleContainer = $('<ul class="w-e-panel-tab-title"></ul>');
        var $tabContentContainer = $('<div class="w-e-panel-tab-content"></div>');
        $container.append($tabTitleContainer).append($tabContentContainer);

        // 设置高度
        var height = opt.height;
        if (height) {
            $tabContentContainer.css('height', height + 'px').css('overflow-y', 'auto');
        }

        // tabs
        var tabs = opt.tabs || [];
        var tabTitleArr = [];
        var tabContentArr = [];
        tabs.forEach(function (tab, tabIndex) {
            if (!tab) {
                return;
            }
            var title = tab.title || '';
            var tpl = tab.tpl || '';

            // 替换多语言
            title = replaceLang(editor, title);
            tpl = replaceLang(editor, tpl);

            // 添加到 DOM
            var $title = $('<li class="w-e-item">' + title + '</li>');
            $tabTitleContainer.append($title);
            var $content = $(tpl);
            $tabContentContainer.append($content);

            // 记录到内存
            $title._index = tabIndex;
            tabTitleArr.push($title);
            tabContentArr.push($content);

            // 设置 active 项
            if (tabIndex === 0) {
                $title._active = true;
                $title.addClass('w-e-active');
            } else {
                $content.hide();
            }

            // 绑定 tab 的事件
            $title.on('click', function (e) {
                if ($title._active) {
                    return;
                }
                // 隐藏所有的 tab
                tabTitleArr.forEach(function ($title) {
                    $title._active = false;
                    $title.removeClass('w-e-active');
                });
                tabContentArr.forEach(function ($content) {
                    $content.hide();
                });

                // 显示当前的 tab
                $title._active = true;
                $title.addClass('w-e-active');
                $content.show();
            });
        });

        // 绑定关闭事件
        $container.on('click', function (e) {
            // 点击时阻止冒泡
            e.stopPropagation();
        });
        $body.on('click', function (e) {
            _this.hide();
        });

        // 添加到 DOM
        $textContainerElem.append($container);

        // 绑定 opt 的事件，只有添加到 DOM 之后才能绑定成功
        tabs.forEach(function (tab, index) {
            if (!tab) {
                return;
            }
            var events = tab.events || [];
            events.forEach(function (event) {
                var selector = event.selector;
                var type = event.type;
                var fn = event.fn || emptyFn;
                var $content = tabContentArr[index];
                $content.find(selector).on(type, function (e) {
                    e.stopPropagation();
                    var needToHide = fn(e);
                    // 执行完事件之后，是否要关闭 panel
                    if (needToHide) {
                        _this.hide();
                    }
                });
            });
        });

        // focus 第一个 elem
        var $inputs = $container.find('input[type=text],textarea');
        if ($inputs.length) {
            $inputs.get(0).focus();
        }

        // 添加到属性
        this.$container = $container;

        // 隐藏其他 panel
        this._hideOtherPanels();
        // 记录该 menu 已经创建了 panel
        _isCreatedPanelMenus.push(menu);
    },

    // 隐藏（移除DOM）
    hide: function hide() {
        var menu = this.menu;
        var $container = this.$container;
        if ($container) {
            $container.remove();
        }

        // 将该 menu 记录中移除
        _isCreatedPanelMenus = _isCreatedPanelMenus.filter(function (item) {
            if (item === menu) {
                return false;
            } else {
                return true;
            }
        });
    },

    // 一个 panel 展示时，隐藏其他 panel
    _hideOtherPanels: function _hideOtherPanels() {
        if (!_isCreatedPanelMenus.length) {
            return;
        }
        _isCreatedPanelMenus.forEach(function (menu) {
            var panel = menu.panel || {};
            if (panel.hide) {
                panel.hide();
            }
        });
    }
};

/*
    menu - link
*/
// 构造函数
function Link(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-link"></i></div>');
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Link.prototype = {
    constructor: Link,

    // 点击事件
    onClick: function onClick(e) {
        var editor = this.editor;
        var $linkelem = void 0;

        if (this._active) {
            // 当前选区在链接里面
            $linkelem = editor.selection.getSelectionContainerElem();
            if (!$linkelem) {
                return;
            }
            // 将该元素都包含在选取之内，以便后面整体替换
            editor.selection.createRangeByElem($linkelem);
            editor.selection.restoreSelection();
            // 显示 panel
            this._createPanel($linkelem.text(), $linkelem.attr('href'));
        } else {
            // 当前选区不在链接里面
            if (editor.selection.isSelectionEmpty()) {
                // 选区是空的，未选中内容
                this._createPanel('', '');
            } else {
                // 选中内容了
                this._createPanel(editor.selection.getSelectionText(), '');
            }
        }
    },

    // 创建 panel
    _createPanel: function _createPanel(text, link) {
        var _this = this;

        // panel 中需要用到的id
        var inputLinkId = getRandom('input-link');
        var inputTextId = getRandom('input-text');
        var btnOkId = getRandom('btn-ok');
        var btnDelId = getRandom('btn-del');

        // 是否显示“删除链接”
        var delBtnDisplay = this._active ? 'inline-block' : 'none';

        // 初始化并显示 panel
        var panel = new Panel(this, {
            width: 300,
            // panel 中可包含多个 tab
            tabs: [{
                // tab 的标题
                title: '链接',
                // 模板
                tpl: '<div>\n                            <input id="' + inputTextId + '" type="text" class="block" value="' + text + '" placeholder="\u94FE\u63A5\u6587\u5B57"/></td>\n                            <input id="' + inputLinkId + '" type="text" class="block" value="' + link + '" placeholder="http://..."/></td>\n                            <div class="w-e-button-container">\n                                <button id="' + btnOkId + '" class="right">\u63D2\u5165</button>\n                                <button id="' + btnDelId + '" class="gray right" style="display:' + delBtnDisplay + '">\u5220\u9664\u94FE\u63A5</button>\n                            </div>\n                        </div>',
                // 事件绑定
                events: [
                // 插入链接
                {
                    selector: '#' + btnOkId,
                    type: 'click',
                    fn: function fn() {
                        // 执行插入链接
                        var $link = $('#' + inputLinkId);
                        var $text = $('#' + inputTextId);
                        var link = $link.val();
                        var text = $text.val();
                        _this._insertLink(text, link);

                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                },
                // 删除链接
                {
                    selector: '#' + btnDelId,
                    type: 'click',
                    fn: function fn() {
                        // 执行删除链接
                        _this._delLink();

                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }]
            } // tab end
            ] // tabs end
        });

        // 显示 panel
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    // 删除当前链接
    _delLink: function _delLink() {
        if (!this._active) {
            return;
        }
        var editor = this.editor;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var selectionText = editor.selection.getSelectionText();
        editor.cmd.do('insertHTML', '<span>' + selectionText + '</span>');
    },

    // 插入链接
    _insertLink: function _insertLink(text, link) {
        var editor = this.editor;
        var config = editor.config;
        var linkCheck = config.linkCheck;
        var checkResult = true; // 默认为 true
        if (linkCheck && typeof linkCheck === 'function') {
            checkResult = linkCheck(text, link);
        }
        if (checkResult === true) {
            editor.cmd.do('insertHTML', '<a href="' + link + '" target="_blank">' + text + '</a>');
        } else {
            alert(checkResult);
        }
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        if ($selectionELem.getNodeName() === 'A') {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    italic-menu
*/
// 构造函数
function Italic(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-italic"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Italic.prototype = {
    constructor: Italic,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;
        var isSeleEmpty = editor.selection.isSelectionEmpty();

        if (isSeleEmpty) {
            // 选区是空的，插入并选中一个“空白”
            editor.selection.createEmptyRange();
        }

        // 执行 italic 命令
        editor.cmd.do('italic');

        if (isSeleEmpty) {
            // 需要将选取折叠起来
            editor.selection.collapseRange();
            editor.selection.restoreSelection();
        }
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor.cmd.queryCommandState('italic')) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    redo-menu
*/
// 构造函数
function Redo(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-redo"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Redo.prototype = {
    constructor: Redo,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;

        // 执行 redo 命令
        editor.cmd.do('redo');
    }
};

/*
    strikeThrough-menu
*/
// 构造函数
function StrikeThrough(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-strikethrough"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
StrikeThrough.prototype = {
    constructor: StrikeThrough,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;
        var isSeleEmpty = editor.selection.isSelectionEmpty();

        if (isSeleEmpty) {
            // 选区是空的，插入并选中一个“空白”
            editor.selection.createEmptyRange();
        }

        // 执行 strikeThrough 命令
        editor.cmd.do('strikeThrough');

        if (isSeleEmpty) {
            // 需要将选取折叠起来
            editor.selection.collapseRange();
            editor.selection.restoreSelection();
        }
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor.cmd.queryCommandState('strikeThrough')) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    underline-menu
*/
// 构造函数
function Underline(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-underline"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Underline.prototype = {
    constructor: Underline,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;
        var isSeleEmpty = editor.selection.isSelectionEmpty();

        if (isSeleEmpty) {
            // 选区是空的，插入并选中一个“空白”
            editor.selection.createEmptyRange();
        }

        // 执行 underline 命令
        editor.cmd.do('underline');

        if (isSeleEmpty) {
            // 需要将选取折叠起来
            editor.selection.collapseRange();
            editor.selection.restoreSelection();
        }
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor.cmd.queryCommandState('underline')) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    undo-menu
*/
// 构造函数
function Undo(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-undo"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Undo.prototype = {
    constructor: Undo,

    // 点击事件
    onClick: function onClick(e) {
        // 点击菜单将触发这里

        var editor = this.editor;

        // 执行 undo 命令
        editor.cmd.do('undo');
    }
};

/*
    menu - list
*/
// 构造函数
function List(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-list2"></i></div>');
    this.type = 'droplist';

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 120,
        $title: $('<p>设置列表</p>'),
        type: 'list', // droplist 以列表形式展示
        list: [{ $elem: $('<span><i class="w-e-icon-list-numbered"></i> 有序列表</span>'), value: 'insertOrderedList' }, { $elem: $('<span><i class="w-e-icon-list2"></i> 无序列表</span>'), value: 'insertUnorderedList' }],
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 List 对象
            _this._command(value);
        }
    });
}

// 原型
List.prototype = {
    constructor: List,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;
        var $textElem = editor.$textElem;
        editor.selection.restoreSelection();
        if (editor.cmd.queryCommandState(value)) {
            return;
        }
        editor.cmd.do(value);

        // 验证列表是否被包裹在 <p> 之内
        var $selectionElem = editor.selection.getSelectionContainerElem();
        if ($selectionElem.getNodeName() === 'LI') {
            $selectionElem = $selectionElem.parent();
        }
        if (/^ol|ul$/i.test($selectionElem.getNodeName()) === false) {
            return;
        }
        if ($selectionElem.equal($textElem)) {
            // 证明是顶级标签，没有被 <p> 包裹
            return;
        }
        var $parent = $selectionElem.parent();
        if ($parent.equal($textElem)) {
            // $parent 是顶级标签，不能删除
            return;
        }

        $selectionElem.insertAfter($parent);
        $parent.remove();
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor.cmd.queryCommandState('insertUnOrderedList') || editor.cmd.queryCommandState('insertOrderedList')) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    menu - justify
*/
// 构造函数
function Justify(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-paragraph-left"></i></div>');
    this.type = 'droplist';

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 100,
        $title: $('<p>对齐方式</p>'),
        type: 'list', // droplist 以列表形式展示
        list: [{ $elem: $('<span><i class="w-e-icon-paragraph-left"></i> 靠左</span>'), value: 'justifyLeft' }, { $elem: $('<span><i class="w-e-icon-paragraph-center"></i> 居中</span>'), value: 'justifyCenter' }, { $elem: $('<span><i class="w-e-icon-paragraph-right"></i> 靠右</span>'), value: 'justifyRight' }],
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 List 对象
            _this._command(value);
        }
    });
}

// 原型
Justify.prototype = {
    constructor: Justify,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;
        editor.cmd.do(value);
    }
};

/*
    menu - Forecolor
*/
// 构造函数
function ForeColor(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-pencil2"></i></div>');
    this.type = 'droplist';

    // 获取配置的颜色
    var config = editor.config;
    var colors = config.colors || [];

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 120,
        $title: $('<p>文字颜色</p>'),
        type: 'inline-block', // droplist 内容以 block 形式展示
        list: colors.map(function (color) {
            return { $elem: $('<i style="color:' + color + ';" class="w-e-icon-pencil2"></i>'), value: color };
        }),
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 ForeColor 对象
            _this._command(value);
        }
    });
}

// 原型
ForeColor.prototype = {
    constructor: ForeColor,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;
        editor.cmd.do('foreColor', value);
    }
};

/*
    menu - BackColor
*/
// 构造函数
function BackColor(editor) {
    var _this = this;

    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-paint-brush"></i></div>');
    this.type = 'droplist';

    // 获取配置的颜色
    var config = editor.config;
    var colors = config.colors || [];

    // 当前是否 active 状态
    this._active = false;

    // 初始化 droplist
    this.droplist = new DropList(this, {
        width: 120,
        $title: $('<p>背景色</p>'),
        type: 'inline-block', // droplist 内容以 block 形式展示
        list: colors.map(function (color) {
            return { $elem: $('<i style="color:' + color + ';" class="w-e-icon-paint-brush"></i>'), value: color };
        }),
        onClick: function onClick(value) {
            // 注意 this 是指向当前的 BackColor 对象
            _this._command(value);
        }
    });
}

// 原型
BackColor.prototype = {
    constructor: BackColor,

    // 执行命令
    _command: function _command(value) {
        var editor = this.editor;
        editor.cmd.do('backColor', value);
    }
};

/*
    menu - quote
*/
// 构造函数
function Quote(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-quotes-left"></i>\n        </div>');
    this.type = 'click';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Quote.prototype = {
    constructor: Quote,

    onClick: function onClick(e) {
        var editor = this.editor;
        var $selectionElem = editor.selection.getSelectionContainerElem();
        var nodeName = $selectionElem.getNodeName();

        if (!UA.isIE()) {
            if (nodeName === 'BLOCKQUOTE') {
                // 撤销 quote
                editor.cmd.do('formatBlock', '<P>');
            } else {
                // 转换为 quote
                editor.cmd.do('formatBlock', '<BLOCKQUOTE>');
            }
            return;
        }

        // IE 中不支持 formatBlock <BLOCKQUOTE> ，要用其他方式兼容
        var content = void 0,
            $targetELem = void 0;
        if (nodeName === 'P') {
            // 将 P 转换为 quote
            content = $selectionElem.text();
            $targetELem = $('<blockquote>' + content + '</blockquote>');
            $targetELem.insertAfter($selectionElem);
            $selectionElem.remove();
            return;
        }
        if (nodeName === 'BLOCKQUOTE') {
            // 撤销 quote
            content = $selectionElem.text();
            $targetELem = $('<p>' + content + '</p>');
            $targetELem.insertAfter($selectionElem);
            $selectionElem.remove();
        }
    },

    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        var reg = /^BLOCKQUOTE$/i;
        var cmdValue = editor.cmd.queryCommandValue('formatBlock');
        if (reg.test(cmdValue)) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    代码 menu - code
*/
// 构造函数
function Code(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-terminal"></i>\n        </div>');
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Code.prototype = {
    constructor: Code,

    onClick: function onClick(e) {
        var editor = this.editor;
        var $startElem = editor.selection.getSelectionStartElem();
        var $endElem = editor.selection.getSelectionEndElem();
        var isSeleEmpty = editor.selection.isSelectionEmpty();
        var selectionText = editor.selection.getSelectionText();
        var $code = void 0;

        if (!$startElem.equal($endElem)) {
            // 跨元素选择，不做处理
            editor.selection.restoreSelection();
            return;
        }
    
        
        if (!isSeleEmpty) {
            // 选取不是空，用 <code> 包裹即可
            $code = $('<code>' + selectionText + '</code>');
            editor.cmd.do('insertElem', $code);
            editor.selection.createRangeByElem($code, false);
            editor.selection.restoreSelection();
            return;
        }

        var class_val = $startElem.parent().attr('class');
      	var language = "";//语言
        if(class_val != null && class_val.length >0){
        	var class_arr = new Array();
            class_arr = class_val.split(' ');//从class中分解出选择的语言
            for(var i=0; i<class_arr.length; i++){
            	var className = class_arr[i].trim();
            	
            	if(className != null && className != ""){
            		if (className.lastIndexOf('lang-', 0) === 0) {
            			language = className.substring(5, className.length); 
    		            break;
    		        }
            	}	
            }
        }
      	
        

        // 选取是空，且没有夸元素选择，则插入 <pre><code></code></prev>
        if (this._active) {
            // 选中状态，将编辑内容
            this._createPanel(language,$startElem.html());
        } else {
            // 未选中状态，将创建内容
            this._createPanel('');
        }
    },

    _createPanel: function _createPanel(language,value) {
        var _this = this;

        // value - 要编辑的内容
        value = value || '';
        var type = !value ? 'new' : 'edit';
        var textId = getRandom('text');
        var btnId = getRandom('btn');
        var btnLanguageId = getRandom('btnLanguage');

        //语言
	    var languageHtml = "";
	    languageHtml += '<select id="'+btnLanguageId+'">';
	    languageHtml += 	'<option value="js" '+ (language == "js" ? 'selected="selected"' : "")	+'>JavaScript</option>';
	    languageHtml += 	'<option value="html"'+ (language == "html" ? 'selected="selected"' : "")	+'>HTML</option>';
	    languageHtml += 	'<option value="css"'+ (language == "css" ? 'selected="selected"' : "")	+'>CSS</option>';
	    languageHtml += 	'<option value="java"'+ (language == "java" ? 'selected="selected"' : "")	+'>Java</option>';
	    languageHtml += 	'<option value="py"'+ (language == "py" ? 'selected="selected"' : "")	+'>Python</option>'; 
	    languageHtml += 	'<option value="php"'+ (language == "php" ? 'selected="selected"' : "")	+'>PHP</option>';
	    languageHtml += 	'<option value="cpp"'+ (language == "cpp" ? 'selected="selected"' : "")	+'>C/C++</option>';
	    languageHtml += 	'<option value="bsh"'+ (language == "bsh" ? 'selected="selected"' : "")	+'>Shell</option>';
	    languageHtml += 	'<option value="go"'+ (language == "go" ? 'selected="selected"' : "")	+'>Go</option>';
	    languageHtml += 	'<option value="rb"'+ (language == "rb" ? 'selected="selected"' : "")	+'>Ruby</option>';
	    languageHtml += 	'<option value="pl"'+ (language == "pl" ? 'selected="selected"' : "")	+'>Perl</option>';
	    languageHtml += 	'<option value="cs"'+ (language == "cs" ? 'selected="selected"' : "")	+'>C#</option>';
	    languageHtml += 	'<option value="xml"'+ (language == "xml" ? 'selected="selected"' : "")	+'>XML</option>';
	    languageHtml += 	'<option value=""'+ (language == "" ? 'selected="selected"' : "")	+'>其它</option>';
		languageHtml += '</select>';
		
        
        
        // panel 的容器
        var $container = $('.w-e-text-container');
        //宽度
        var div_width = $container.getSizeData().width;
        div_width = div_width*90/100;//90%        
        
        var panel = new Panel(this, {
            width: div_width,
            // 一个 Panel 包含多个 tab
            tabs: [{
                // 标题
                title: '插入代码',
                // 模板
                tpl: '<div>\n                        <textarea id="' + textId + '" style="height:145px;;">' + value + '</textarea>\n                        <div class="w-e-button-container">\n                            <div class="left" >'+languageHtml+'</div>\n                                <button id="' + btnId + '" class="right">\u63D2\u5165</button>\n                        </div>\n                    <div>',
                // 事件绑定
                events: [
                // 插入代码
                {
                    selector: '#' + btnId,
                    type: 'click',
                    fn: function fn() {
                    	var $language = $('#' + btnLanguageId);//语言
                        var $text = $('#' + textId);
                        var text = $text.val() || $text.html();
                        text = replaceHtmlSymbol(text);
                        var _language = $language.val();
                        
                        if (type === 'new') {
                            // 新插入
                            _this._insertCode(_language,text);
                        } else {
                            // 编辑更新
                            _this._updateCode(_language,text);
                        }

                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }]
            } // first tab end
            ] // tabs end
        }); // new Panel end

        // 显示 panel
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    // 插入代码
    _insertCode: function _insertCode(language,value) {
    	var editor = this.editor;
        var cls =' lang-' + language;
        editor.cmd.do('insertHTML', '<br>');
        editor.cmd.do('insertHTML', '<pre class="prettyprint' + cls + '"><code>' + value + '</code></pre><p><br></p>');
    },

    // 更新代码
    _updateCode: function _updateCode(language,value) {
        var editor = this.editor;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        $selectionELem.html(value);
        
        var cls ='prettyprint lang-' + language;
        $selectionELem.parent().attr("class",cls);
        editor.selection.restoreSelection();
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var $parentElem = $selectionELem.parent();
        if ($selectionELem.getNodeName() === 'CODE' && $parentElem.getNodeName() === 'PRE') {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
    menu - emoticon
*/
// 构造函数
function Emoticon(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-happy"></i>\n        </div>');
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Emoticon.prototype = {
    constructor: Emoticon,

    onClick: function onClick() {
    	
        this._createPanel();
       
    },

    _createPanel: function _createPanel() {
        var _this = this;

        var editor = this.editor;
        var config = editor.config;
        // 获取表情配置
        var emotions = config.emotions || [];

        // 创建表情 dropPanel 的配置
        var tabConfig = [];
        emotions.forEach(function (emotData) {
            var emotType = emotData.type;
            var content = emotData.content || [];

            // 这一组表情最终拼接出来的 html
            var faceHtml = '';

            // emoji 表情
            if (emotType === 'emoji') {
                content.forEach(function (item) {
                    if (item) {
                        faceHtml += '<span class="w-e-item">' + item + '</span>';
                    }
                });
            }
            // 图片表情
            if (emotType === 'image') {
                content.forEach(function (item) {
                    var alt = item.alt;
                    var src = item.src;
                    var width = item.width;
                    var height = item.height;
                    if (src) {
                        // 加一个 data-w-e 属性，点击图片的时候不再提示编辑图片
                        faceHtml += '<span class="w-e-item"><img width="' + width + '" height="' + height + '" src="' + src + '" alt="' + alt + '" data-w-e="1"/></span>';
                    }
                });
            }

            tabConfig.push({
                title: emotData.title,
                tpl: '<div class="w-e-emoticon-container">' + faceHtml + '</div>',
                events: [{
                    selector: 'span.w-e-item',
                    type: 'click',
                    fn: function fn(e) {
                        var target = e.target;
                        var $target = $(target);
                        var nodeName = $target.getNodeName();

                        var insertHtml = void 0;
                        if (nodeName === 'IMG') {
                            // 插入图片
                            insertHtml = $target.parent().html();
                        } else {
                            // 插入 emoji
                            insertHtml = '<span>' + $target.html() + '</span>';
                        }

                        _this._insert(insertHtml);
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }]
            });
        });

        var panel = new Panel(this, {
            width: 300,
            height: 200,
            // 一个 Panel 包含多个 tab
            tabs: tabConfig
        });

        // 显示 panel
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    // 插入表情
    _insert: function _insert(emotHtml) {
        var editor = this.editor;
        editor.cmd.do('insertHTML', emotHtml);
    }
};

/*
    menu - table
*/
// 构造函数
function Table(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-table2"></i></div>');
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Table.prototype = {
    constructor: Table,

    onClick: function onClick() {
        if (this._active) {
            // 编辑现有表格
            this._createEditPanel();
        } else {
            // 插入新表格
            this._createInsertPanel();
        }
    },

    // 创建插入新表格的 panel
    _createInsertPanel: function _createInsertPanel() {
        var _this = this;

        // 用到的 id
        var btnInsertId = getRandom('btn');
        var textRowNum = getRandom('row');
        var textColNum = getRandom('col');

        var panel = new Panel(this, {
            width: 250,
            // panel 包含多个 tab
            tabs: [{
                // 标题
                title: '插入表格',
                // 模板
                tpl: '<div>\n                        <p style="text-align:left; padding:5px 0;">\n                            \u521B\u5EFA\n                            <input id="' + textRowNum + '" type="text" value="5" style="width:40px;text-align:center;"/>\n                            \u884C\n                            <input id="' + textColNum + '" type="text" value="5" style="width:40px;text-align:center;"/>\n                            \u5217\u7684\u8868\u683C\n                        </p>\n                        <div class="w-e-button-container">\n                            <button id="' + btnInsertId + '" class="right">\u63D2\u5165</button>\n                        </div>\n                    </div>',
                // 事件绑定
                events: [{
                    // 点击按钮，插入表格
                    selector: '#' + btnInsertId,
                    type: 'click',
                    fn: function fn() {
                        var rowNum = parseInt($('#' + textRowNum).val());
                        var colNum = parseInt($('#' + textColNum).val());

                        if (rowNum && colNum && rowNum > 0 && colNum > 0) {
                            // form 数据有效
                            _this._insert(rowNum, colNum);
                        }

                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }]
            } // first tab end
            ] // tabs end
        }); // panel end

        // 展示 panel
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    // 插入表格
    _insert: function _insert(rowNum, colNum) {
        // 拼接 table 模板
        var r = void 0,
            c = void 0;
        var html = '<table border="0" width="100%" cellpadding="0" cellspacing="0">';
        for (r = 0; r < rowNum; r++) {
            html += '<tr>';
            if (r === 0) {
                for (c = 0; c < colNum; c++) {
                    html += '<th>&nbsp;</th>';
                }
            } else {
                for (c = 0; c < colNum; c++) {
                    html += '<td>&nbsp;</td>';
                }
            }
            html += '</tr>';
        }
        html += '</table><p><br></p>';

        // 执行命令
        var editor = this.editor;
        editor.cmd.do('insertHTML', html);

        // 防止 firefox 下出现 resize 的控制点
        editor.cmd.do('enableObjectResizing', false);
        editor.cmd.do('enableInlineTableEditing', false);
    },

    // 创建编辑表格的 panel
    _createEditPanel: function _createEditPanel() {
        var _this2 = this;

        // 可用的 id
        var addRowBtnId = getRandom('add-row');
        var addColBtnId = getRandom('add-col');
        var delRowBtnId = getRandom('del-row');
        var delColBtnId = getRandom('del-col');
        var delTableBtnId = getRandom('del-table');

        // 创建 panel 对象
        var panel = new Panel(this, {
            width: 320,
            // panel 包含多个 tab
            tabs: [{
                // 标题
                title: '编辑表格',
                // 模板
                tpl: '<div>\n                        <div class="w-e-button-container" style="border-bottom:1px solid #f1f1f1;padding-bottom:5px;margin-bottom:5px;">\n                            <button id="' + addRowBtnId + '" class="left">\u589E\u52A0\u884C</button>\n                            <button id="' + delRowBtnId + '" class="red left">\u5220\u9664\u884C</button>\n                            <button id="' + addColBtnId + '" class="left">\u589E\u52A0\u5217</button>\n                            <button id="' + delColBtnId + '" class="red left">\u5220\u9664\u5217</button>\n                        </div>\n                        <div class="w-e-button-container">\n                            <button id="' + delTableBtnId + '" class="gray left">\u5220\u9664\u8868\u683C</button>\n                        </dv>\n                    </div>',
                // 事件绑定
                events: [{
                    // 增加行
                    selector: '#' + addRowBtnId,
                    type: 'click',
                    fn: function fn() {
                        _this2._addRow();
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }, {
                    // 增加列
                    selector: '#' + addColBtnId,
                    type: 'click',
                    fn: function fn() {
                        _this2._addCol();
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }, {
                    // 删除行
                    selector: '#' + delRowBtnId,
                    type: 'click',
                    fn: function fn() {
                        _this2._delRow();
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }, {
                    // 删除列
                    selector: '#' + delColBtnId,
                    type: 'click',
                    fn: function fn() {
                        _this2._delCol();
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }, {
                    // 删除表格
                    selector: '#' + delTableBtnId,
                    type: 'click',
                    fn: function fn() {
                        _this2._delTable();
                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                }]
            }]
        });
        // 显示 panel
        panel.show();
    },

    // 获取选中的单元格的位置信息
    _getLocationData: function _getLocationData() {
        var result = {};
        var editor = this.editor;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var nodeName = $selectionELem.getNodeName();
        if (nodeName !== 'TD' && nodeName !== 'TH') {
            return;
        }

        // 获取 td index
        var $tr = $selectionELem.parent();
        var $tds = $tr.children();
        var tdLength = $tds.length;
        $tds.forEach(function (td, index) {
            if (td === $selectionELem[0]) {
                // 记录并跳出循环
                result.td = {
                    index: index,
                    elem: td,
                    length: tdLength
                };
                return false;
            }
        });

        // 获取 tr index
        var $tbody = $tr.parent();
        var $trs = $tbody.children();
        var trLength = $trs.length;
        $trs.forEach(function (tr, index) {
            if (tr === $tr[0]) {
                // 记录并跳出循环
                result.tr = {
                    index: index,
                    elem: tr,
                    length: trLength
                };
                return false;
            }
        });

        // 返回结果
        return result;
    },

    // 增加行
    _addRow: function _addRow() {
        // 获取当前单元格的位置信息
        var locationData = this._getLocationData();
        if (!locationData) {
            return;
        }
        var trData = locationData.tr;
        var $currentTr = $(trData.elem);
        var tdData = locationData.td;
        var tdLength = tdData.length;

        // 拼接即将插入的字符串
        var newTr = document.createElement('tr');
        var tpl = '',
            i = void 0;
        for (i = 0; i < tdLength; i++) {
            tpl += '<td>&nbsp;</td>';
        }
        newTr.innerHTML = tpl;
        // 插入
        $(newTr).insertAfter($currentTr);
    },

    // 增加列
    _addCol: function _addCol() {
        // 获取当前单元格的位置信息
        var locationData = this._getLocationData();
        if (!locationData) {
            return;
        }
        var trData = locationData.tr;
        var tdData = locationData.td;
        var tdIndex = tdData.index;
        var $currentTr = $(trData.elem);
        var $trParent = $currentTr.parent();
        var $trs = $trParent.children();

        // 遍历所有行
        $trs.forEach(function (tr) {
            var $tr = $(tr);
            var $tds = $tr.children();
            var $currentTd = $tds.get(tdIndex);
            var name = $currentTd.getNodeName().toLowerCase();

            // new 一个 td，并插入
            var newTd = document.createElement(name);
            $(newTd).insertAfter($currentTd);
        });
    },

    // 删除行
    _delRow: function _delRow() {
        // 获取当前单元格的位置信息
        var locationData = this._getLocationData();
        if (!locationData) {
            return;
        }
        var trData = locationData.tr;
        var $currentTr = $(trData.elem);
        $currentTr.remove();
    },

    // 删除列
    _delCol: function _delCol() {
        // 获取当前单元格的位置信息
        var locationData = this._getLocationData();
        if (!locationData) {
            return;
        }
        var trData = locationData.tr;
        var tdData = locationData.td;
        var tdIndex = tdData.index;
        var $currentTr = $(trData.elem);
        var $trParent = $currentTr.parent();
        var $trs = $trParent.children();

        // 遍历所有行
        $trs.forEach(function (tr) {
            var $tr = $(tr);
            var $tds = $tr.children();
            var $currentTd = $tds.get(tdIndex);
            // 删除
            $currentTd.remove();
        });
    },

    // 删除表格
    _delTable: function _delTable() {
        var editor = this.editor;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var $table = $selectionELem.parentUntil('table');
        if (!$table) {
            return;
        }
        $table.remove();
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var nodeName = $selectionELem.getNodeName();
        if (nodeName === 'TD' || nodeName === 'TH') {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};

/*
 * 视频
    menu - video
*/
// 构造函数
function Video(editor) {
    this.editor = editor;
    this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-play"></i></div>');
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Video.prototype = {
    constructor: Video,

    onClick: function onClick() {
        this._createPanel();
    },

    _createPanel: function _createPanel() {
        var _this = this;
        var editor = this.editor;
        var config = editor.config;
        var isEmbedVideo = false;//是否允许嵌入视频
        var isUploadVideo = false;//是否允许上传视频
    	for(var i=0; i< config.menus.length; i++){
			var menu = config.menus[i];
			if(menu == "embedVideo"){
				isEmbedVideo = true;
			}else if(menu == "uploadVideo"){
				isUploadVideo = true;
			}
		}
      
        
        
        // 创建 id
        var textValId = getRandom('text-val');
        var btnId = getRandom('btn');
        var inputUploadId = getRandom('input-upload');
        var btnUploadId = getRandom('btn-upload');
        
        // 是否显示嵌入视频框
        var embedVideoDisplay = isEmbedVideo ? 'inline-block' : 'none';
        // 是否显示上传按钮
        var uploadBtnDisplay = isUploadVideo ? 'inline-block' : 'none';
        
        // 创建 panel
        var panel = new Panel(this, {
            width: 350,
            // 一个 panel 多个 tab
            tabs: [{
                // 标题
                title: '插入视频',
                // 模板
                tpl: '<div>\n                        <input id="' + textValId + '" style="display:' + embedVideoDisplay + '" type="text" class="block" placeholder="\u683C\u5F0F\u5982\uFF1A<iframe src=... ></iframe>"/>\n                        <div class="w-e-button-container">\n                            <button id="' + btnUploadId + '" class="left" style="display:' + uploadBtnDisplay + '">\u4E0A\u4F20</button>\n                                <button id="' + btnId + '" style="display:' + embedVideoDisplay + '" class="right">\u63D2\u5165</button>\n                        </div>\n                            <div style="display:none;">\n                                <input id="' + inputUploadId + '" type="file"/>\n                    </div>',
                // 事件绑定
                events: [{
                    selector: '#' + btnId,
                    type: 'click',
                    fn: function fn() {
                        var $text = $('#' + textValId);
                        var val = $text.val().trim();

                        // 测试用视频地址
                        // <iframe height=498 width=510 src='http://player.youku.com/embed/XMjcwMzc3MzM3Mg==' frameborder=0 'allowfullscreen'></iframe>

                        if (val) {
                            // 插入视频
                            _this._insert(val);
                        }

                        // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                        return true;
                    }
                },
                // 上传文件
                {
                    selector: '#' + btnUploadId,
                    type: 'click',
                    fn: function fn() {
                        var $file = $('#' + inputUploadId);
                        var fileElem = $file[0];
                        if (fileElem) {
                            fileElem.click();
                        } else {
                            // 返回 true 可关闭 panel
                            return true;
                        }
                    }
                }, {
                    // 选择文件完毕
                    selector: '#' + inputUploadId,
                    type: 'change',
                    fn: function fn() {
                        var $file = $('#' + inputUploadId);
                        var fileElem = $file[0];
                        if (!fileElem) {
                            // 返回 true 可关闭 panel
                            return true;
                        }

                        // 获取选中的 file 对象列表
                        var fileList = fileElem.files;
                        if (fileList.length) {
                           // uploadVideo.uploadVideo(fileList, inputLinkId);
                        	_this._uploadVideo(fileList);
                        }

                        // 返回 true 可关闭 panel
                        return true
                    }
                }]
            } // first tab end
            ] // tabs end
        }); // panel end

        // 显示 panel
        panel.show();

        // 记录属性
        this.panel = panel;
    },
    
    // 上传视频
    _uploadVideo: function _uploadVideo(files) {
	    if (!files || !files.length) {
	        return;
	    }
	    
	    // ------------------------------ 获取配置信息 ------------------------------
	    var editor = this.editor;
	    var config = editor.config;
	    var customUploadVideo = config.customUploadVideo;
	    if (!customUploadVideo) {
	        this._alert('请配置customUploadVideo参数!');
	        return;
	    }
	   
	    // ------------------------------ 自定义上传 ------------------------------
	    if (customUploadVideo && typeof customUploadVideo === 'function') {
	    	customUploadVideo(files, this._insertLinkVideo.bind(this));

	        // 阻止以下代码执行
	        return;
	    }
    	
    	
    	
    	
    	
    },
    // 嵌入视频代码
    _insert: function _insert(val) {
        var editor = this.editor;
        editor.cmd.do('insertHTML', val + '<p><br></p>');
    },
    // 插入上传视频代码
    _insertLinkVideo: function _insertLinkVideo(link) {
    	var editor = this.editor;
    	if (!link) {
            return;
        }
    	
    	//preload ="none" 提示浏览器不需要缓存视频    同时设置初始内容时.txt.html()里将所有video标签都加上preload ="none"属性
    	//editor.cmd.do('insertHTML', '<video src="' + link + '" controls="controls" preload ="none" /><p><br></p>');
    	
    	//获取Javascript文件自身所在URL路径
    	var scripts = document.scripts;
    	var url =scripts[scripts.length - 1].src;
    	url = url.substring(0, url.lastIndexOf('/'));
    	
    	//将视频标签转为指定图片标签
    	editor.cmd.do('insertHTML', '<img class="playerImg" src="'+url+'/wangeditor/play.png" data="' + link + '" /><p><br></p>');
    	

    	
    },
    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
    	
    	
    }
    
};

/*
    menu - img
*/
// 构造函数
function Image(editor) {
    this.editor = editor;
    var imgMenuId = getRandom('w-e-img');
    this.$elem = $('<div class="w-e-menu" id="' + imgMenuId + '"><i class="w-e-icon-image"></i></div>');
    editor.imgMenuId = imgMenuId;
    this.type = 'panel';

    // 当前是否 active 状态
    this._active = false;
}

// 原型
Image.prototype = {
    constructor: Image,

    onClick: function onClick() {
        var editor = this.editor;
        var config = editor.config;
        if (config.qiniu) {
            return;
        }
        if (this._active) {
            this._createEditPanel();
        } else {
            this._createInsertPanel();
        }
    },

    _createEditPanel: function _createEditPanel() {
        var editor = this.editor;

        //如果是视频标签转图片，则不执行
        var _img = editor._selectedImg;
        if (_img && _img.attr("class") =="playerImg") {
        	return;
        }
        
        // id
        var width30 = getRandom('width-30');
        var width50 = getRandom('width-50');
        var width100 = getRandom('width-100');
        var delBtn = getRandom('del-btn');

        // tab 配置
        var tabsConfig = [{
            title: '编辑图片',
            tpl: '<div>\n                    <div class="w-e-button-container" style="border-bottom:1px solid #f1f1f1;padding-bottom:5px;margin-bottom:5px;">\n                        <span style="float:left;font-size:14px;margin:4px 5px 0 5px;color:#333;">\u6700\u5927\u5BBD\u5EA6\uFF1A</span>\n                        <button id="' + width30 + '" class="left">30%</button>\n                        <button id="' + width50 + '" class="left">50%</button>\n                        <button id="' + width100 + '" class="left">100%</button>\n                    </div>\n                    <div class="w-e-button-container">\n                        <button id="' + delBtn + '" class="gray left">\u5220\u9664\u56FE\u7247</button>\n                    </dv>\n                </div>',
            events: [{
                selector: '#' + width30,
                type: 'click',
                fn: function fn() {
                    var $img = editor._selectedImg;
                    if ($img) {
                        $img.css('max-width', '30%');
                    }
                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                    return true;
                }
            }, {
                selector: '#' + width50,
                type: 'click',
                fn: function fn() {
                    var $img = editor._selectedImg;
                    if ($img) {
                        $img.css('max-width', '50%');
                    }
                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                    return true;
                }
            }, {
                selector: '#' + width100,
                type: 'click',
                fn: function fn() {
                    var $img = editor._selectedImg;
                    if ($img) {
                        $img.css('max-width', '100%');
                    }
                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                    return true;
                }
            }, {
                selector: '#' + delBtn,
                type: 'click',
                fn: function fn() {
                    var $img = editor._selectedImg;
                    if ($img) {
                        $img.remove();
                    }
                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                    return true;
                }
            }]
        }];

        // 创建 panel 并显示
        var panel = new Panel(this, {
            width: 300,
            tabs: tabsConfig
        });
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    _createInsertPanel: function _createInsertPanel() {
        var editor = this.editor;
        var uploadImg = editor.uploadImg;
        var config = editor.config;

        // id
        var upTriggerId = getRandom('up-trigger');
        var upFileId = getRandom('up-file');
        var linkUrlId = getRandom('link-url');
        var linkBtnId = getRandom('link-btn');

        // tabs 的配置
        var tabsConfig = [{
            title: '上传图片',
            tpl: '<div class="w-e-up-img-container">\n                    <div id="' + upTriggerId + '" class="w-e-up-btn">\n                        <i class="w-e-icon-upload2"></i>\n                    </div>\n                    <div style="display:none;">\n                        <input id="' + upFileId + '" type="file" multiple="multiple" accept="image/jpg,image/jpeg,image/png,image/gif,image/bmp"/>\n                    </div>\n                </div>',
            events: [{
                // 触发选择图片
                selector: '#' + upTriggerId,
                type: 'click',
                fn: function fn() {
                    var $file = $('#' + upFileId);
                    var fileElem = $file[0];
                    if (fileElem) {
                        fileElem.click();
                    } else {
                        // 返回 true 可关闭 panel
                        return true;
                    }
                }
            }, {
                // 选择图片完毕
                selector: '#' + upFileId,
                type: 'change',
                fn: function fn() {
                    var $file = $('#' + upFileId);
                    var fileElem = $file[0];
                    if (!fileElem) {
                        // 返回 true 可关闭 panel
                        return true;
                    }

                    // 获取选中的 file 对象列表
                    var fileList = fileElem.files;
                    if (fileList.length) {
                        uploadImg.uploadImg(fileList);
                    }

                    // 返回 true 可关闭 panel
                    return true;
                }
            }]
        }, // first tab end
        {
            title: '网络图片',
            tpl: '<div>\n                    <input id="' + linkUrlId + '" type="text" class="block" placeholder="\u56FE\u7247\u94FE\u63A5"/></td>\n                    <div class="w-e-button-container">\n                        <button id="' + linkBtnId + '" class="right">\u63D2\u5165</button>\n                    </div>\n                </div>',
            events: [{
                selector: '#' + linkBtnId,
                type: 'click',
                fn: function fn() {
                    var $linkUrl = $('#' + linkUrlId);
                    var url = $linkUrl.val().trim();

                    if (url) {
                        uploadImg.insertLinkImg(url);
                    }

                    // 返回 true 表示函数执行结束之后关闭 panel
                    return true;
                }
            }]
        } // second tab end
        ]; // tabs end

        // 判断 tabs 的显示
        var tabsConfigResult = [];
        if ((config.uploadImgShowBase64 || config.uploadImgServer || config.customUploadImg) && window.FileReader) {
            // 显示“上传图片”
            tabsConfigResult.push(tabsConfig[0]);
        }
        if (config.showLinkImg) {
            // 显示“网络图片”
            tabsConfigResult.push(tabsConfig[1]);
        }

        // 创建 panel 并显示
        var panel = new Panel(this, {
            width: 300,
            tabs: tabsConfigResult
        });
        panel.show();

        // 记录属性
        this.panel = panel;
    },

    // 试图改变 active 状态
    tryChangeActive: function tryChangeActive(e) {
        var editor = this.editor;
        var $elem = this.$elem;
        if (editor._selectedImg) {
            this._active = true;
            $elem.addClass('w-e-active');
        } else {
            this._active = false;
            $elem.removeClass('w-e-active');
        }
    }
};


/*
menu - file
*/
//构造函数
function File(editor) {
this.editor = editor;
this.$elem = $('<div class="w-e-menu"><i class="w-e-icon-file"></i></div>');
this.type = 'panel';

// 当前是否 active 状态
this._active = false;
}

//原型
File.prototype = {
 constructor: File,

 // 点击事件
 onClick: function onClick(e) {
     var editor = this.editor;
     var $linkelem = void 0;

     if (this._active) {
         // 当前选区在链接里面
         $linkelem = editor.selection.getSelectionContainerElem();
         if (!$linkelem) {
             return;
         }
         // 将该元素都包含在选取之内，以便后面整体替换
         editor.selection.createRangeByElem($linkelem);
         editor.selection.restoreSelection();
         // 显示 panel
         this._createPanel($linkelem.text(), $linkelem.attr('href'));
     } else {
         // 当前选区不在链接里面
         if (editor.selection.isSelectionEmpty()) {
             // 选区是空的，未选中内容
             this._createPanel('', '');
         } else {
             // 选中内容了
             this._createPanel(editor.selection.getSelectionText(), '');
         }
     }
 },

 // 创建 panel
 _createPanel: function _createPanel(text, link) {
     var _this = this;

     var editor = this.editor;
     var uploadFile = editor.uploadFile;
     var config = editor.config;
     // panel 中需要用到的id
     var inputFileId = getRandom('input-file');
     var inputFileDescriptionId = getRandom('input-fileDescription');
     var inputUploadId = getRandom('input-upload');
     var btnOkId = getRandom('btn-ok');
     var btnDelId = getRandom('btn-del');
     var btnUploadId = getRandom('btn-upload');

     // 是否显示“删除链接”
     var delBtnDisplay = this._active ? 'inline-block' : 'none';

     // 初始化并显示 panel
     var panel = new Panel(this, {
         width: 300,
         // panel 中可包含多个 tab
         tabs: [{
             // tab 的标题
             title: '插入文件',
             // 模板
             tpl: '<div>\n                            <input id="' + inputFileDescriptionId + '" type="text" class="block" value="' + text + '" placeholder="\u6587\u4ef6\u8bf4\u660e"/></td>\n                            <input id="' + inputFileId + '" type="hidden" class="block" value="' + link + '" placeholder="http://..."/></td>\n                            <div class="w-e-button-container">\n                                <button id="' + btnOkId + '" class="right">\u63D2\u5165</button>\n                                <button id="' + btnUploadId + '" class="left" style="display:inline-block">\u9009\u62e9\u6587\u4ef6</button>\n                                <button id="' + btnDelId + '" class="gray right" style="display:' + delBtnDisplay + '">\u5220\u9664\u94FE\u63A5</button>\n                            </div>\n                            <div style="display:none;">\n                                <input id="' + inputUploadId + '" type="file"/>\n                            </div>\n                        </div>',
             // 事件绑定
             events: [
             // 插入链接
             {
                 selector: '#' + btnOkId,
                 type: 'click',
                 fn: function fn() {
                     // 执行插入链接
                     var $link = $('#' + inputFileId);
                     var $text = $('#' + inputFileDescriptionId);
                     var link = $link.val();
                     var text = $text.val();
                     _this._insertLink(text, link);

                     // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                     return true;
                 }
             },
             // 上传文件
             {
                 selector: '#' + btnUploadId,
                 type: 'click',
                 fn: function fn() {
                     var $file = $('#' + inputUploadId);
                     var fileElem = $file[0];
                     if (fileElem) {
                         fileElem.click();
                     } else {
                         // 返回 true 可关闭 panel
                         return true;
                     }
                 }
             }, {
                 // 选择文件完毕
                 selector: '#' + inputUploadId,
                 type: 'change',
                 fn: function fn() {
                     var $file = $('#' + inputUploadId);
                     var fileElem = $file[0];
                     if (!fileElem) {
                         // 返回 true 可关闭 panel
                         return true;
                     }
                     
                     // 获取选中的 file 对象列表
                     var fileList = fileElem.files;
                     if (fileList.length) {
                         uploadFile.uploadFile(fileList, inputFileId,inputFileDescriptionId);
                     }

                     // 返回 true 可关闭 panel
                     // return true
                 }
             },
             // 删除链接
             {
                 selector: '#' + btnDelId,
                 type: 'click',
                 fn: function fn() {
                     // 执行删除链接
                     _this._delLink();

                     // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
                     return true;
                 }
             }] // tab end
         }] // tabs end
     });

     // 显示 panel
     panel.show();

     // 记录属性
     this.panel = panel;
 },

 // 删除当前链接
 _delLink: function _delLink() {
     if (!this._active) {
         return;
     }
     var editor = this.editor;
     var $selectionELem = editor.selection.getSelectionContainerElem();
     if (!$selectionELem) {
         return;
     }
     var selectionText = editor.selection.getSelectionText();
     editor.cmd.do('insertHTML', '<span>' + selectionText + '</span>');
 },

 // 插入链接
 _insertLink: function _insertLink(text, link) {
     var editor = this.editor;
     var config = editor.config;
     var linkCheck = config.linkCheck;
     var checkResult = true; // 默认为 true
     if (linkCheck && typeof linkCheck === 'function') {
         checkResult = linkCheck(text, link);
     }
     if (checkResult === true) {
         editor.cmd.do('insertHTML', '<a href="' + link + '" target="_blank">' + text + '</a>');
     } else {
         alert(checkResult);
     }
 },

 // 试图改变 active 状态
 tryChangeActive: function tryChangeActive(e) {
     var editor = this.editor;
     var $elem = this.$elem;
     var $selectionELem = editor.selection.getSelectionContainerElem();
     if (!$selectionELem) {
         return;
     }
     if ($selectionELem.getNodeName() === 'A') {
         this._active = true;
         $elem.addClass('w-e-active');
     } else {
         this._active = false;
         $elem.removeClass('w-e-active');
     }
 }
};



/*
menu - hide
*/
//构造函数
function Hide(editor) {
	this.editor = editor;
	this.$elem = $('<div class="w-e-menu">\n            <i class="w-e-icon-hide"></i>\n        </div>');
	this.type = 'panel';
	// 当前是否 active 状态
	this._active = false;
}

//原型
Hide.prototype = {
	constructor: Hide,
	
	// 点击事件
	onClick: function onClick(e) {
	    var editor = this.editor;
	    if (this._active) {//选中隐藏标签
	    	var $selectionELem = editor.selection.getSelectionContainerElem();
	        if (!$selectionELem) {
	            return;
	        }
	        
	        var $hide = $selectionELem.parentUntil('HIDE');
	        if ($hide != null){
	        	var visibleType = $($hide).attr('hide-type');
	 	        var inputValue = $($hide).attr('input-value')
	        	
	 	        this._createPanel(visibleType, inputValue);
	        }else{
	        	var visibleType = $selectionELem.attr('hide-type');
		        var inputValue = $selectionELem.attr('input-value');
		        
		    	this._createPanel(visibleType, inputValue);
	        }
	        
	        
	        
	        
	    } else {//没有选中隐藏标签
	    	var config = editor.config;
	    	var visibleType = null;
	    	var visibleType_10 = null;
	    	var visibleType_20 = null;
	    	var visibleType_30 = null;
	    	var visibleType_40 = null;
	    	var visibleType_50 = null;
	    	for(var i=0; i< config.menus.length; i++){
				var menu = config.menus[i];
				if(menu == "hidePassword"){
					visibleType_10 = 10;
				}else if(menu == "hideComment"){
					visibleType_20 = 20;
				}else if(menu == "hideGrade"){
					visibleType_30 = 30;
				}else if(menu == "hidePoint"){
					visibleType_40 = 40;
				}else if(menu == "hideAmount"){
					visibleType_50 = 50;
				}
			}
	    	if(visibleType_50 != null){
	    		visibleType = visibleType_50;
	    	}
	    	if(visibleType_40 != null){
	    		visibleType = visibleType_40;
	    	}
	    	if(visibleType_30 != null){
	    		visibleType = visibleType_30;
	    	}
	    	if(visibleType_20 != null){
	    		visibleType = visibleType_20;
	    	}
	    	if(visibleType_10 != null){
	    		visibleType = visibleType_10;
	    	}
	    	
	    	this._createPanel(visibleType, "");
	        
	    }
	},
	
	// 创建 panel
	//visibleType 隐藏类型  inputValue
	_createPanel: function _createPanel(visibleType,inputValue) {
	    var _this = this;
	    var editor = this.editor;
	    var config = editor.config;
        
	    // panel 中需要用到的id
	    var inputPasswordId = getRandom('input-inputValue_10');//密码
	    var inputGradeId = getRandom('input-inputValue_30');//达到等级
	    var inputPointId = getRandom('input-inputValue_40');//积分购买
	    var inputAmountId = getRandom('input-inputValue_50');//余额购买
	    
	     
	    var btnOkId = getRandom('btnOkId');//插入标签
	    var btnEditId = getRandom('btnEditId');//修改标签
	    var btnDelId = getRandom('btnDelId');//删除标签
	    
	    
	    var visibleTypeName = getRandom('radio-visibleType');
	    var visibleType_10 = getRandom('radio-visibleType_10');
	    var visibleType_20 = getRandom('radio-visibleType_20');
	    var visibleType_30 = getRandom('radio-visibleType_30');
	    var visibleType_40 = getRandom('radio-visibleType_40');
	    var visibleType_50 = getRandom('radio-visibleType_50');
	    
	    var inputValueBox_10 = getRandom('inputValueBox_10');
	    var inputValueBox_20 = getRandom('inputValueBox_20');
	    var inputValueBox_30 = getRandom('inputValueBox_30');
	    var inputValueBox_40 = getRandom('inputValueBox_40');
	    var inputValueBox_50 = getRandom('inputValueBox_50');
	    
	    
	    // 是否显示“删除”
	    var delBtnDisplay = 'inline-block';
	    // 是否显示“修改隐藏标签”
	    var editBtnDisplay = 'inline-block';
	    // 是否显示“插入”
	    var addBtnDisplay = 'inline-block';
	    
	    if (this._active) {//选中隐藏标签
	    	delBtnDisplay = 'inline-block';
	    	editBtnDisplay = 'inline-block';
	    	addBtnDisplay = 'none'; 
	    } else {//没有选中隐藏标签
	    	delBtnDisplay = 'none';
	    	editBtnDisplay = 'none'; 
	    	addBtnDisplay = 'inline-block';
	    }
	    
	    var visibleType_checked_html_10 = "";
	    var visibleType_checked_html_20 = "";
	    var visibleType_checked_html_30 = "";
	    var visibleType_checked_html_40 = "";
	    var visibleType_checked_html_50 = "";
	    if(visibleType == 10){
	    	visibleType_checked_html_10 = " checked=checked ";
	    }else if(visibleType == 20){
	    	visibleType_checked_html_20 = " checked=checked ";
	    }else if(visibleType == 30){
	    	visibleType_checked_html_30 = " checked=checked ";
	    }else if(visibleType == 40){
	    	visibleType_checked_html_40 = " checked=checked ";
	    }else if(visibleType == 50){
	    	visibleType_checked_html_50 = " checked=checked ";
	    }
	    
	    
	    var inputValue_password = "";
	    var inputValue_grade = "";
	    var inputValue_point = "";
	    var inputValue_amount = "";
	    if(visibleType == 10){
	    	inputValue_password = inputValue;
	    }else if(visibleType == 20){
	    	
	    }else if(visibleType == 30){
	    	inputValue_grade = inputValue;
	    }else if(visibleType == 40){
	    	inputValue_point = inputValue;
	    }else if(visibleType == 50){
	    	inputValue_amount = inputValue;
	    }
	    
	    //等级
	    var userGradeHtml = "";
	    var userGradeList = config.userGradeList;
		if(userGradeList != null && userGradeList.length >0){
			userGradeHtml += '<select id="'+inputGradeId+'">';
			for(var i=0; i<userGradeList.length; i++){
				var userGrade = userGradeList[i];
					
				userGradeHtml += '<option value="'+userGrade.needPoint+'" '+ (inputValue_grade == userGrade.needPoint ? 'selected="selected"' : "")	+'>'+userGrade.name+'</option>';
			}
			userGradeHtml += '</select>';
		}
	    
	    
	    var template = "<div>";
	    
	    for(var i=0; i< config.menus.length; i++){
			var menu = config.menus[i];
			if(menu == "hidePassword"){
				template += 	"<label>";
		    	template += 		"<div class='radio-box'><input type='radio' id='"+visibleType_10+"' name='"+visibleTypeName+"' class='radio-input' value='10' "+visibleType_checked_html_10+"> <span class='radio-core'></span><span class='radio-title'>输入密码可见</span></div>";
		    	template += 	"</label>";
			}else if(menu == "hideComment"){
				template += 	"<label>";
		    	template += 		"<div class='radio-box'><input type='radio' id='"+visibleType_20+"' name='"+visibleTypeName+"' class='radio-input' value='20' "+visibleType_checked_html_20+"> <span class='radio-core'></span><span class='radio-title'>回复话题可见</span></div>";
		    	template += 	"</label>";
			}else if(menu == "hideGrade"){
				template += 	"<label>";
		    	template += 		"<div class='radio-box'><input type='radio' id='"+visibleType_30+"' name='"+visibleTypeName+"' class='radio-input' value='30' "+visibleType_checked_html_30+"> <span class='radio-core'></span><span class='radio-title'>达到等级可见</span></div>";
		    	template += 	"</label>";
			}else if(menu == "hidePoint"){
				template += 	"<label>";
		    	template += 		"<div class='radio-box'><input type='radio' id='"+visibleType_40+"' name='"+visibleTypeName+"' class='radio-input' value='40' "+visibleType_checked_html_40+"> <span class='radio-core'></span><span class='radio-title'>积分购买可见</span></div>";
		    	template += 	"</label>";
			}else if(menu == "hideAmount"){
		    	template += 	"<label>";
		    	template += 		"<div class='radio-box'><input type='radio' id='"+visibleType_50+"' name='"+visibleTypeName+"' class='radio-input' value='50' "+visibleType_checked_html_50+"> <span class='radio-core'></span><span class='radio-title'>余额购买可见</span></div>";
		    	template += 	"</label>";
			}
		}

	    	template += 	"</td>";
	    	
	    	template += 		"<span id='"+inputValueBox_10+"' style='display:none;'><input id='"+inputPasswordId+"'  type='text' class='block' value='"+inputValue_password+"' placeholder='密码' maxlength='20' /></span>";
	    	template += 		"<span id='"+inputValueBox_30+"' style='display:none;'>等级达到 "+userGradeHtml+" 以上可见</span>";
	    	
	    	template += 		"<span id='"+inputValueBox_40+"' style='display:none;'>需要支付 <input id='"+inputPointId+"' style='width:40px;text-align:center;' type='text' value='"+inputValue_point+"' maxlength='8'/> 积分可见</span>";
	    	template += 		"<span id='"+inputValueBox_50+"' style='display:none;'>需要支付 <input id='"+inputAmountId+"' style='width:40px;text-align:center;' type='text' value='"+inputValue_amount+"' maxlength='9'/> 元费用可见</span>";
	    	template += 	"</td>";
	    	
	    	template += 	"<div class='w-e-button-container'>";
	    	template += 		"<button id='"+btnOkId+"' class='right' style='display:" + addBtnDisplay + "'>插入</button>";
	    	template += 		"<button id='"+btnEditId+"' class='right' style='display:" + editBtnDisplay + "'>修改</button>";
	    	template += 		"<button id='"+btnDelId+"' class='right' style='display:" + delBtnDisplay + "'>删除</button>";
	    	template += 	"</div>";
	    	template += " </div>";
	    	
	    	template += "";
	    

	    // 初始化并显示 panel
	    	var panel = new Panel(this, {
	    	width: 300,

	        // panel 中可包含多个 tab
	        tabs: [{
	            // tab 的标题
	            title: '隐藏',
	            // 模板
	            tpl: template,
	            // 事件绑定
	            events: [
	            // 选择隐藏类型
	            {
	                selector: '#' + visibleType_10,
	                type: 'click',
	                fn: function fn() {
	                    var $inputValueBox_10 = $('#' + inputValueBox_10).show();
	                    var $inputValueBox_30 = $('#' + inputValueBox_30).hide();
	                    var $inputValueBox_40 = $('#' + inputValueBox_40).hide();
	                    var $inputValueBox_50 = $('#' + inputValueBox_50).hide();
	                }
	            },
	            {
	            	selector: '#' + visibleType_20,
	                type: 'click',
	                fn: function fn() {
	                	var $inputValueBox_10 = $('#' + inputValueBox_10).hide();
	                    var $inputValueBox_30 = $('#' + inputValueBox_30).hide();
	                    var $inputValueBox_40 = $('#' + inputValueBox_40).hide();
	                    var $inputValueBox_50 = $('#' + inputValueBox_50).hide();
	                }
	            },
	            {
	                selector: '#' + visibleType_30,
	                type: 'click',
	                fn: function fn() {
	                	var $inputValueBox_10 = $('#' + inputValueBox_10).hide();
	                    var $inputValueBox_30 = $('#' + inputValueBox_30).show();
	                    var $inputValueBox_40 = $('#' + inputValueBox_40).hide();
	                    var $inputValueBox_50 = $('#' + inputValueBox_50).hide();
	                }
	            },
	            {
	                selector: '#' + visibleType_40,
	                type: 'click',
	                fn: function fn() {
	                	var $inputValueBox_10 = $('#' + inputValueBox_10).hide();
	                    var $inputValueBox_30 = $('#' + inputValueBox_30).hide();
	                    var $inputValueBox_40 = $('#' + inputValueBox_40).show();
	                    var $inputValueBox_50 = $('#' + inputValueBox_50).hide();
	                }
	            },
	            {
	                selector: '#' + visibleType_50,
	                type: 'click',
	                fn: function fn() {
	                	var $inputValueBox_10 = $('#' + inputValueBox_10).hide();
	                    var $inputValueBox_30 = $('#' + inputValueBox_30).hide();
	                    var $inputValueBox_40 = $('#' + inputValueBox_40).hide();
	                    var $inputValueBox_50 = $('#' + inputValueBox_50).show();
	                }
	            },
	            // 插入标签
	            {
	                selector: '#' + btnOkId,
	                type: 'click',
	                fn: function fn() {
	                	var _visibleTypeName = $("input[name='"+visibleTypeName+"']:checked").val();
	                	var _inputValue = "";
	                	
	                	if(_visibleTypeName == 10){
	                		 _inputValue = $('#' + inputPasswordId).val();//密码
	                		 if(_inputValue.trim() == ""){
	                			 alert("请输入密码");
	                			 return false;
	                		 }
	                	}else if(_visibleTypeName == 20){
	                		 
	                	}else if(_visibleTypeName == 30){
	                		_inputValue = $('#' + inputGradeId).val();//达到等级
	                		if (_inputValue == "" || !/^[0-9]*[1-9][0-9]*$/.test(_inputValue)) {
	                			alert("请选择等级");
	                			return false;
	                		}
	                	}else if(_visibleTypeName == 40){
	                		_inputValue  = $('#' + inputPointId).val();//积分购买
	                		if (_inputValue == "" || !/^[0-9]*[1-9][0-9]*$/.test(_inputValue)) {
		     					alert("请输入大于0的数字");
		     					return false;
		                	}
	                	}else if(_visibleTypeName == 50){
	                		_inputValue = $('#' + inputAmountId).val();//余额购买
	                		if (_inputValue == "" || !/^(([1-9]\d*)(\.\d{1,2})?)$|(0\.0?([1-9]\d?))$/.test(_inputValue)) {
	                			 alert("请输入大于0的金额");
	                			 return false;
	                		}
	                	}
	                	
	                	//等级标签
						var gradeTag = "";
						if(userGradeList != null && userGradeList.length >0){
							for(var i=0; i<userGradeList.length; i++){
								var userGrade = userGradeList[i];
								if(userGrade.needPoint == _inputValue){
									gradeTag = userGrade.name;
								}
							}
						}
	                	
	                	var html = "";
	                	if(_visibleTypeName == 10){//输入密码可见
							html = "<hide class='inputValue_10' hide-type='10' input-value='"+_inputValue+"'></hide>";
						}else if(_visibleTypeName == 20){//回复话题可见
							html = "<hide class='inputValue_20' hide-type='20' ></hide>";
						}else if(_visibleTypeName == 30){//达到等级可见
							html = "<hide class='inputValue_30' hide-type='30' input-value='"+_inputValue+"' description='"+gradeTag+"'></hide>";
						}else if(_visibleTypeName == 40){//积分购买可见
							html = "<hide class='inputValue_40' hide-type='40' input-value='"+_inputValue+"'></hide>";
						}else if(_visibleTypeName == 50){//余额购买可见
							html = "<hide class='inputValue_50' hide-type='50' input-value='"+_inputValue+"'></hide>";
						}
	                	_this._insertHide(html);
	                	var htmlBefore = _this._processHtmlBefore(editor.txt.html());
	                	var htmlFormat = _this._htmlFormat(htmlBefore);
	                	
	                	editor.txt.html(_this._processHtmlAfter(htmlFormat));
	                	
	                	// 设置同类型标签为相同的值
	        	        _this._setSameTypeTab(editor.txt.html(),_visibleTypeName,_inputValue,gradeTag);

	                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
	                    return true;
	                }
	            },
	            // 修改标签
	            {
	            	selector: '#' + btnEditId,
	                type: 'click',
	                fn: function fn() {
	                	var _visibleTypeName = $("input[name='"+visibleTypeName+"']:checked").val();
	                	var _inputValue = "";
	                	
	                	if(_visibleTypeName == 10){
	                		 _inputValue = $('#' + inputPasswordId).val();//密码
	                		 if(_inputValue.trim() == ""){
	                			 alert("请输入密码");
	                			 return false;
	                		 }
	                	}else if(_visibleTypeName == 20){
	                		 
	                	}else if(_visibleTypeName == 30){
	                		_inputValue = $('#' + inputGradeId).val();//达到等级
	                		if (_inputValue == "" || !/^\d*$/.test(_inputValue)) {
	                			alert("请选择等级");
	                			return false;
	                		}
	                	}else if(_visibleTypeName == 40){
	                		_inputValue  = $('#' + inputPointId).val();//积分购买
	                		if (_inputValue == "" || !/^\d*$/.test(_inputValue)) {
		     					alert("请输入数字类型");
		     					return false;
		                	}
	                	}else if(_visibleTypeName == 50){
	                		_inputValue = $('#' + inputAmountId).val();//余额购买
	                		if (_inputValue == "" || !/^\d*$/.test(_inputValue)) {
	                			 alert("请输入数字类型");
	                			 return false;
	                		}
	                	}
	                	
	                	//等级标签
						var gradeTag = "";
						if(userGradeList != null && userGradeList.length >0){
							for(var i=0; i<userGradeList.length; i++){
								var userGrade = userGradeList[i];
								if(userGrade.needPoint == _inputValue){
									gradeTag = userGrade.name;
								}
							}
						}
	                	
	                	var $hide = editor.selection.getSelectionContainerElem().parentUntil('HIDE');
	        	        if ($hide != null){
	        	 	       if(_visibleTypeName == 10){//输入密码可见
		                		$hide.attr('class','inputValue_10');
		                		$hide.attr('hide-type',10);
		                		$hide.attr('input-value',_inputValue);
							}else if(_visibleTypeName == 20){//回复话题可见
								$hide.attr('class','inputValue_20');
								$hide.attr('hide-type',20);
								$hide.attr('input-value','');
							}else if(_visibleTypeName == 30){//达到等级可见
								$hide.attr('class','inputValue_30');
								$hide.attr('hide-type',30);
								$hide.attr('input-value',_inputValue);
								$hide.attr('description',gradeTag);
							}else if(_visibleTypeName == 40){//积分购买可见
								$hide.attr('class','inputValue_40');
								$hide.attr('hide-type',40);
								$hide.attr('input-value',_inputValue);
							}else if(_visibleTypeName == 50){//余额购买可见
								$hide.attr('class','inputValue_50');
								$hide.attr('hide-type',50);
								$hide.attr('input-value',_inputValue);
							} 
	        	        }else{
	        	        	if(_visibleTypeName == 10){//输入密码可见
		                		var $selectionELem = editor.selection.getSelectionContainerElem();
		                		$selectionELem.attr('class','inputValue_10');
		            	        $selectionELem.attr('hide-type',10);
		            	        $selectionELem.attr('input-value',_inputValue);
							}else if(_visibleTypeName == 20){//回复话题可见
								var $selectionELem = editor.selection.getSelectionContainerElem();
		                		$selectionELem.attr('class','inputValue_20');
		            	        $selectionELem.attr('hide-type',20);
		            	        $selectionELem.attr('input-value','');
							}else if(_visibleTypeName == 30){//达到等级可见
								var $selectionELem = editor.selection.getSelectionContainerElem();
		                		$selectionELem.attr('class','inputValue_30');
		            	        $selectionELem.attr('hide-type',30);
		            	        $selectionELem.attr('input-value',_inputValue);
		            	        $selectionELem.attr('description',gradeTag);
							}else if(_visibleTypeName == 40){//积分购买可见
								var $selectionELem = editor.selection.getSelectionContainerElem();
		                		$selectionELem.attr('class','inputValue_40');
		            	        $selectionELem.attr('hide-type',40);
		            	        $selectionELem.attr('input-value',_inputValue);
							}else if(_visibleTypeName == 50){//余额购买可见
								var $selectionELem = editor.selection.getSelectionContainerElem();
		                		$selectionELem.attr('class','inputValue_50');
		            	        $selectionELem.attr('hide-type',50);
		            	        $selectionELem.attr('input-value',_inputValue);
							}
	        	        	
	        	        }
	        	        // 设置同类型标签为相同的值
	        	        _this._setSameTypeTab(editor.txt.html(),_visibleTypeName,_inputValue,gradeTag);
	                	
	                    // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
	                	return true;
	                }
	            },
	             // 删除隐藏标签
	             {
	                 selector: '#' + btnDelId,
	                 type: 'click',
	                 fn: function fn() {
	                	 //删除标签
	                	 _this._delHide();
	                     // 返回 true，表示该事件执行完之后，panel 要关闭。否则 panel 不会关闭
	                     return true;
	                 }
	            }]

	        } // tab end
	        ] // tabs end
	    });
	
	
	    // 显示 panel
	    panel.show();
	    
	    //显示输入值框
	    if(visibleType == 10){
		    $('#' + inputValueBox_10).show();
	    }else if(visibleType == 20){
	    	
	    }else if(visibleType == 30){
	    	$('#' + inputValueBox_30).show();
	    }else if(visibleType == 40){
	    	$('#' + inputValueBox_40).show();
	    }else if(visibleType == 50){
	    	$('#' + inputValueBox_50).show();
	    }
	    
	    // 记录属性
	    this.panel = panel;
	},
	
	// 删除标签
	_delHide: function _delHide() {
	    if (!this._active) {
	        return;
	    }
	    var editor = this.editor;
        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        var $hide = $selectionELem.parentUntil('HIDE');
        if (!$hide) {
        	$selectionELem.remove();
        }else{
        	
        	$hide.remove();
        }
        
	},
	
	// 插入标签
	_insertHide: function _insertHide(html) {
		
		var editor = this.editor;
        editor.cmd.do('insertHTML', html);

        var $selectionELem = editor.selection.getSelectionContainerElem();
        if (!$selectionELem) {
            return;
        }
        editor.selection.saveRange();
     // 插入 <p> ，并将选取定位到 <p>
        var $p = $('<p><br></p>');
        $p.insertAfter($selectionELem);
        editor.selection.createRangeByElem($p, true);
        editor.selection.restoreSelection();// 恢复选区
        

   //     editor.cmd.do('insertHTML', '<p><br></p>');
	},
	
	
	
	// 处理html前
	_processHtmlBefore: function _processHtmlBefore(html) {
		return html.replace(/(<(?:hide|hide\s[^>]*)>)([\s\S]*?)(<\/hide>)/ig, function($0, $1, $2, $3) {
			//因为<p>标签包裹<pre>等块元素时会被浏览器截断，<hide>标签内含的<pre>在“<p><hide>aaa<pre>bbb</pre>ccc</hide></p>”被浏览器执行node.innerHTML后变为“<p><hide>aaa</hide></p><pre>bbb</pre>ccc<p></p>”,发生错位。 
			//我们将<hide>标签用块元素<article>包裹,让<hide>的父级<p>标签自动截断,保护<hide>标签内的<pre>标签不会由截断产生错位。正确结果为“<p></p><hide>aaa<pre>bbb</pre>ccc</hide><p></p>”
			//本正则和方法self.beforeGetHtml配合使用
			return '<article type="__kindeditor_temp_pre__">' +$1 + $2 + $3 + '</article>';
		});
	},
	// 处理html后
	_processHtmlAfter: function _processHtmlAfter(html) {
		return html.replace(/<article\s+[^>]*type="([^"]*)"[^>]*>([\s\S]*?)<\/article>/ig, function(full, attr, code) {//将self.beforeSetHtml方法加入的<article>标签删除
			return code;
		});
	},
	//html格式化（解决P标签内不能包含块级元素问题）
	_htmlFormat: function _htmlFormat(html) {
		var node = document.createElement("div");
		node.innerHTML = html;
		
		return node.innerHTML;
	},
	
	
	// 设置同类型标签为相同的值
	_setSameTypeTab: function _setSameTypeTab(oldHtml,visibleType,inputValue,description) {
		var _this = this;
		if(visibleType == 10){//输入密码可见
			//替换标签
			var htmlValue = _this.replaceTab(oldHtml,"hide","inputValue_10",""+inputValue+"");
			_this.editor.txt.html(htmlValue);
		}else if(visibleType == 20){//回复话题可见
			
		}else if(visibleType == 30){//达到等级可见
			//替换标签
			var htmlValue =  _this.replaceTab(oldHtml,"hide","inputValue_30",""+inputValue+"",""+description+"");
			_this.editor.txt.html(htmlValue);
		}else if(visibleType == 40){//积分购买可见
			var htmlValue =  _this.replaceTab(oldHtml,"hide","inputValue_40",""+inputValue+""); 
			_this.editor.txt.html(htmlValue);
		}else if(visibleType == 50){//余额购买可见
			var htmlValue =  _this.replaceTab(oldHtml,"hide","inputValue_50",""+inputValue+""); 
			_this.editor.txt.html(htmlValue);
		}
		
	},
	/**
	 * 替换标签
	 * html html内容
	 * tag 标签名称
	 * className css样式名称
	 * inputValue 替换标签值
	 * description 描述
	 */
	replaceTab: function replaceTab(html,tag,className,inputValue,description) {
		var _this = this;
		var node = document.createElement("div");
		node.innerHTML = html;
		
		_this.getChildNode(node,tag,className,inputValue,description);
		return node.innerHTML;
	},
	
	/**
	 * 递归获取所有的子节点
	 * node 节点
	 * tag 标签名称
	 * className css样式名称
	 * inputValue 替换标签值
	 * description 描述
	 */
	getChildNode: function getChildNode(node,tag,className,inputValue,description) {
        //先找到子节点
        var nodeList = node.childNodes;
        for(var i = 0;i < nodeList.length;i++){
            //childNode获取到到的节点包含了各种类型的节点
            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
            var childNode = nodeList[i];
            
            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
            if(childNode.nodeType == 1){
            	if(childNode.nodeName.toLowerCase() == tag.toLowerCase() && 
            			childNode.getAttribute("class") == className){
            		childNode.setAttribute("input-value",inputValue);
            		if(description != null && description != ""){
            			childNode.setAttribute("description",description);
            		}
            	}
                getChildNode(childNode,tag,className,inputValue,description);
            }
        }
    },
	
    /**
   	 * 跳出<hide>标签
   	 * event 事件
   	 * node 选中节点
   	 */
    jumpHideLabel: function jumpHideLabel(event,node,menuElem) {
   	var editor = this.editor;
       var elem = node[0];
       var nodeName = node.getNodeName();
       if(nodeName == "HIDE"){
           elem = node[0];
       }else{
           var $hide = node.parentUntil('HIDE');
           if ($hide != null){
               elem = $hide[0];
           }
       }

	  
	   //<hide>标签左下角相对屏幕坐标X轴
 	   var hide_clientRect_x = elem.getBoundingClientRect().left; 
 	   //<hide>标签左下角相对屏幕坐标Y轴
 	   var hide_clientRect_y = elem.getBoundingClientRect().bottom; 
 	   //小按钮宽
	    var buttonWidth = 12;
	    //小按钮高
	    var buttonWidth = 12;
	    //小按钮左上角坐标X轴
	    var button_left_top_x = hide_clientRect_x-12;
	    //小按钮左上角坐标Y轴
	    var button_left_top_y = hide_clientRect_y-12;
	    //小按钮右下角坐标X轴
	    var button_right_bottom_x = hide_clientRect_x;
	    //小按钮右下角坐标Y轴
	    var button_right_bottom_y = hide_clientRect_y;
 	   
       
	    //判断鼠标的坐标是否在这一区域
	  	if( event.clientX > button_left_top_x && event.clientX <button_right_bottom_x && event.clientY >button_left_top_y && event.clientY < button_right_bottom_y){
	  		/**
	         var editor = this.editor;
	         var $textElem = editor.$textElem;
	         var $children = $textElem.children();
	         var $last = $children.last();
	         $textElem.append($('<p><br></p>'));
	         editor.selection.createRangeByElem($last, false, true);
	         editor.selection.restoreSelection();
	 	    **/
           this._active = false;
           menuElem.removeClass('w-e-active');
	 	    
	 	    
	 	    var editor = this.editor;
	 		    
	 	    var $containerElem = editor.selection.getSelectionContainerElem();
	 	    // 判断选区内容是否在编辑内容之内
	         if (!$containerElem) {
	             return;
	         }
	 	    
            /**
	 	    var parent = $containerElem;

           while (parent.parent()) {
               var nodeName = parent.getNodeName();
               if(nodeName == "HIDE"){
                   parent = parent.parent();
                   break;
               }
               if(nodeName == "BODY"){
                   parent = null;
                   break;
               }
               parent = parent.parent();
           } */
	 	    
           var current = node;
           if($containerElem.getNodeName() != "HIDE"){
               var $hide = node.parentUntil('HIDE');
               if ($hide != null){
                   current = $hide;
               }
           }

           var $p = $('<p><br></p>');
           
           $p.insertAfter(current);
           editor.selection.createRangeByElem( $p, false,true);
	        editor.selection.restoreSelection();
	         
	  	}
 	   
	  	
   },
	
	
// 试图改变 active 状态
   tryChangeActive: function tryChangeActive(e) {
   	var editor = this.editor;
       var $elem = this.$elem;
       var $selectionELem = editor.selection.getSelectionContainerElem();
       if (!$selectionELem) {
           return;
       }
       
       var $hide = $selectionELem.parentUntil('HIDE');
       if ($hide != null){
           this._active = true;
           $elem.addClass('w-e-active');
           this.jumpHideLabel(e,$selectionELem,$elem);
           return;
      }
      var nodeName = $selectionELem.getNodeName();
      if (nodeName === 'HIDE') {
          this._active = true;
          $elem.addClass('w-e-active');
          this.jumpHideLabel(e,$selectionELem,$elem);
      } else {
          this._active = false;
          $elem.removeClass('w-e-active');
      }

       /**
       var $hide = $selectionELem.parentUntil('HIDE');
       console.log("试图改变 active 状态2--",$hide);
      
       if ($hide != null){
       	 this._active = true;
            $elem.addClass('w-e-active');
       	 return;
       }
     
       var nodeName = $selectionELem.getNodeName();
       console.log("试图改变 active 状态3",nodeName);
       if (nodeName === 'HIDE') {
           this._active = true;
           $elem.addClass('w-e-active');
           this.jumpHideLabel(e,$selectionELem);
       } else {
           this._active = false;
           $elem.removeClass('w-e-active');
       }**/

   }
	
};




/*
    所有菜单的汇总
*/

// 存储菜单的构造函数
var MenuConstructors = {};

MenuConstructors.bold = Bold;

MenuConstructors.head = Head;

MenuConstructors.fontSize = FontSize;

MenuConstructors.fontName = FontName;

MenuConstructors.link = Link;

MenuConstructors.italic = Italic;

MenuConstructors.redo = Redo;

MenuConstructors.strikeThrough = StrikeThrough;

MenuConstructors.underline = Underline;

MenuConstructors.undo = Undo;

MenuConstructors.list = List;

MenuConstructors.justify = Justify;

MenuConstructors.foreColor = ForeColor;

MenuConstructors.backColor = BackColor;

MenuConstructors.quote = Quote;

MenuConstructors.code = Code;

MenuConstructors.emoticon = Emoticon;

MenuConstructors.table = Table;

MenuConstructors.video = Video;

MenuConstructors.image = Image;

MenuConstructors.file = File;
MenuConstructors.hide = Hide;
/*
    菜单集合
*/
// 构造函数
function Menus(editor) {
    this.editor = editor;
    this.menus = {};
}

// 修改原型
Menus.prototype = {
    constructor: Menus,

    // 初始化菜单
    init: function init() {
        var _this = this;

        var editor = this.editor;
        var config = editor.config || {};
        var configMenus = config.menus || []; // 获取配置中的菜单

        // 根据配置信息，创建菜单
        configMenus.forEach(function (menuKey) {
            var MenuConstructor = MenuConstructors[menuKey];
            if (MenuConstructor && typeof MenuConstructor === 'function') {
                // 创建单个菜单
                _this.menus[menuKey] = new MenuConstructor(editor);
            }
        });

        // 添加到菜单栏
        this._addToToolbar();

        // 绑定事件
        this._bindEvent();
    },

    // 添加到菜单栏
    _addToToolbar: function _addToToolbar() {
        var editor = this.editor;
        var $toolbarElem = editor.$toolbarElem;
        var menus = this.menus;
        var config = editor.config;
        // config.zIndex 是配置的编辑区域的 z-index，菜单的 z-index 得在其基础上 +1
        var zIndex = config.zIndex + 1;
        objForEach(menus, function (key, menu) {
            var $elem = menu.$elem;
            if ($elem) {
                // 设置 z-index
            //    $elem.css('z-index', zIndex);//不设置菜单栏的z-index
                $toolbarElem.append($elem);
            }
        });
    },

    // 绑定菜单 click mouseenter 事件
    _bindEvent: function _bindEvent() {
        var menus = this.menus;
        var editor = this.editor;
        objForEach(menus, function (key, menu) {
            var type = menu.type;
            if (!type) {
                return;
            }
            var $elem = menu.$elem;
            var droplist = menu.droplist;
            var panel = menu.panel;

            // 点击类型，例如 bold
            if (type === 'click' && menu.onClick) {
                $elem.on('click', function (e) {
                    if (editor.selection.getRange() == null) {
                        return;
                    }
                    menu.onClick(e);
                });
            }

            // 下拉框，例如 head
            if (type === 'droplist' && droplist) {
                $elem.on('mouseenter', function (e) {
                    if (editor.selection.getRange() == null) {
                        return;
                    }
                    // 显示
                    droplist.showTimeoutId = setTimeout(function () {
                        droplist.show();
                    }, 200);
                }).on('mouseleave', function (e) {
                    // 隐藏
                    droplist.hideTimeoutId = setTimeout(function () {
                        droplist.hide();
                    }, 0);
                });
            }

            // 弹框类型，例如 link
            if (type === 'panel' && menu.onClick) {
                $elem.on('click', function (e) {
                    e.stopPropagation();
                    if (editor.selection.getRange() == null) {
                        return;
                    }
                    // 在自定义事件中显示 panel
                    menu.onClick(e);
                });
            }
        });
    },

    // 尝试修改菜单状态
    changeActive: function changeActive() {
        var menus = this.menus;
        var event = window.event;
        objForEach(menus, function (key, menu) {
            if (menu.tryChangeActive) {
                setTimeout(function () {
                    menu.tryChangeActive(event);
                }, 100);
            }
        });
    }
};

/*
    粘贴信息的处理
*/

// 获取粘贴的纯文本
function getPasteText(e) {
    var clipboardData = e.clipboardData || e.originalEvent && e.originalEvent.clipboardData;
    var pasteText = void 0;
    if (clipboardData == null) {
        pasteText = window.clipboardData && window.clipboardData.getData('text');
    } else {
        pasteText = clipboardData.getData('text/plain');
    }

    return replaceHtmlSymbol(pasteText);
}

// 获取粘贴的html
function getPasteHtml(e, filterStyle, ignoreImg) {
    var clipboardData = e.clipboardData || e.originalEvent && e.originalEvent.clipboardData;
    var pasteText = void 0,
        pasteHtml = void 0;
    if (clipboardData == null) {
        pasteText = window.clipboardData && window.clipboardData.getData('text');
    } else {
        pasteText = clipboardData.getData('text/plain');
        pasteHtml = clipboardData.getData('text/html');
    }
    if (!pasteHtml && pasteText) {
        pasteHtml = '<p>' + replaceHtmlSymbol(pasteText) + '</p>';
    }
    if (!pasteHtml) {
        return;
    }

    // 过滤word中状态过来的无用字符
    var docSplitHtml = pasteHtml.split('</html>');
    if (docSplitHtml.length === 2) {
        pasteHtml = docSplitHtml[0];
    }

    // 过滤无用标签
    pasteHtml = pasteHtml.replace(/<(meta|script|link).+?>/igm, '');
    // 去掉注释
    pasteHtml = pasteHtml.replace(/<!--.*?-->/mg, '');
    // 过滤 data-xxx 属性
    pasteHtml = pasteHtml.replace(/\s?data-.+?=('|").+?('|")/igm, '');

    if (ignoreImg) {
        // 忽略图片
        pasteHtml = pasteHtml.replace(/<img.+?>/igm, '');
    }

    if (filterStyle) {
        // 过滤样式
        pasteHtml = pasteHtml.replace(/\s?(class|style)=('|").*?('|")/igm, '');
    } else {
        // 保留样式
        pasteHtml = pasteHtml.replace(/\s?class=('|").*?('|")/igm, '');
    }

    return pasteHtml;
}

// 获取粘贴的图片文件
function getPasteImgs(e) {
    var result = [];
    var txt = getPasteText(e);
    if (txt) {
        // 有文字，就忽略图片
        return result;
    }

    var clipboardData = e.clipboardData || e.originalEvent && e.originalEvent.clipboardData || {};
    var items = clipboardData.items;
    if (!items) {
        return result;
    }

    objForEach(items, function (key, value) {
        var type = value.type;
        if (/image/i.test(type)) {
            result.push(value.getAsFile());
        }
    });

    return result;
}

/*
    编辑区域
*/

// 获取一个 elem.childNodes 的 JSON 数据
function getChildrenJSON($elem) {
    var result = [];
    var $children = $elem.childNodes() || []; // 注意 childNodes() 可以获取文本节点
    $children.forEach(function (curElem) {
        var elemResult = void 0;
        var nodeType = curElem.nodeType;

        // 文本节点
        if (nodeType === 3) {
            elemResult = curElem.textContent;
            elemResult = replaceHtmlSymbol(elemResult);
        }

        // 普通 DOM 节点
        if (nodeType === 1) {
            elemResult = {};

            // tag
            elemResult.tag = curElem.nodeName.toLowerCase();
            // attr
            var attrData = [];
            var attrList = curElem.attributes || {};
            var attrListLength = attrList.length || 0;
            for (var i = 0; i < attrListLength; i++) {
                var attr = attrList[i];
                attrData.push({
                    name: attr.name,
                    value: attr.value
                });
            }
            elemResult.attrs = attrData;
            // children（递归）
            elemResult.children = getChildrenJSON($(curElem));
        }

        result.push(elemResult);
    });
    return result;
}

// 构造函数
function Text(editor) {
    this.editor = editor;
}

// 修改原型
Text.prototype = {
    constructor: Text,

    // 初始化
    init: function init() {
        // 绑定事件
        this._bindEvent();
    },

    // 清空内容
    clear: function clear() {
        this.html('<p><br></p>');
    },

    // 获取 设置 html
    html: function html(val) {
        var editor = this.editor;
        var $textElem = editor.$textElem;
        var html = void 0;
        if (val == null) {
            html = $textElem.html();
            // 未选中任何内容的时候点击“加粗”或者“斜体”等按钮，就得需要一个空的占位符 &#8203 ，这里替换掉
            html = html.replace(/\u200b/gm, '');
            return html;
        } else {
            $textElem.html(val);

            // 初始化选取，将光标定位到内容尾部
            editor.initSelection();
        }
    },

    // 获取 JSON
    getJSON: function getJSON() {
        var editor = this.editor;
        var $textElem = editor.$textElem;
        return getChildrenJSON($textElem);
    },

    // 获取 设置 text
    text: function text(val) {
        var editor = this.editor;
        var $textElem = editor.$textElem;
        var text = void 0;
        if (val == null) {
            text = $textElem.text();
            // 未选中任何内容的时候点击“加粗”或者“斜体”等按钮，就得需要一个空的占位符 &#8203 ，这里替换掉
            text = text.replace(/\u200b/gm, '');
            return text;
        } else {
            $textElem.text('<p>' + val + '</p>');

            // 初始化选取，将光标定位到内容尾部
            editor.initSelection();
        }
    },

    // 追加内容
    append: function append(html) {
        var editor = this.editor;
        var $textElem = editor.$textElem;
        $textElem.append($(html));

        // 初始化选取，将光标定位到内容尾部
        editor.initSelection();
    },

    // 绑定事件
    _bindEvent: function _bindEvent() {
        // 实时保存选取
        this._saveRangeRealTime();

        // 按回车建时的特殊处理
        this._enterKeyHandle();

        // 清空时保留 <p><br></p>
        this._clearHandle();

        // 粘贴事件（粘贴文字，粘贴图片）
        this._pasteHandle();

        // tab 特殊处理
        this._tabHandle();

        // img 点击
        this._imgHandle();

        
        // 拖拽事件
        this._dragHandle();
    },

    // 实时保存选取
    _saveRangeRealTime: function _saveRangeRealTime() {
        var editor = this.editor;
        var $textElem = editor.$textElem;

        // 保存当前的选区
        function saveRange(e) {
            // 随时保存选区
            editor.selection.saveRange();
            // 更新按钮 ative 状态
            editor.menus.changeActive();
        }
        // 按键后保存
        $textElem.on('keyup', saveRange);
        $textElem.on('mousedown', function (e) {
            // mousedown 状态下，鼠标滑动到编辑区域外面，也需要保存选区
            $textElem.on('mouseleave', saveRange);
        });
        $textElem.on('mouseup', function (e) {
            saveRange();
            // 在编辑器区域之内完成点击，取消鼠标滑动到编辑区外面的事件
            $textElem.off('mouseleave', saveRange);
        });
    },

    // 按回车键时的特殊处理
    _enterKeyHandle: function _enterKeyHandle() {
        var editor = this.editor;
        var $textElem = editor.$textElem;

        function insertEmptyP($selectionElem) {
            var $p = $('<p><br></p>');
            $p.insertBefore($selectionElem);
            editor.selection.createRangeByElem($p, true);
            editor.selection.restoreSelection();
            $selectionElem.remove();
        }

        // 将回车之后生成的非 <p> 的顶级标签，改为 <p>
        function pHandle(e) {
            var $selectionElem = editor.selection.getSelectionContainerElem();
            var $parentElem = $selectionElem.parent();

            if ($parentElem.html() === '<code><br></code>') {
                // 回车之前光标所在一个 <p><code>.....</code></p> ，忽然回车生成一个空的 <p><code><br></code></p>
                // 而且继续回车跳不出去，因此只能特殊处理
                insertEmptyP($selectionElem);
                return;
            }
            
            var nodeName = $selectionElem.getNodeName();
            if (nodeName === 'HIDE') {
            	
                return;
            }
            /**
            var nodeName = $selectionElem.getNodeName();
            if (nodeName === 'HIDE') {
            	 // 回车之前光标所在一个 <p><hide>.....</hide></p> ，忽然回车生成一个空的 <p><hide><br></hide></p>
                // 而且继续回车跳不出去，因此只能特殊处理
                insertEmptyP($selectionElem);
                return;
            }**/

            if (!$parentElem.equal($textElem)) {
                // 不是顶级标签
                return;
            }

            var nodeName = $selectionElem.getNodeName();
            if (nodeName === 'P') {
                // 当前的标签是 P ，不用做处理
                return;
            }
           

            if ($selectionElem.text()) {
                // 有内容，不做处理
                return;
            }

            // 插入 <p> ，并将选取定位到 <p>，删除当前标签
            insertEmptyP($selectionElem);
        }

        $textElem.on('keyup', function (e) {
            if (e.keyCode !== 13) {
                // 不是回车键
                return;
            }
            // 将回车之后生成的非 <p> 的顶级标签，改为 <p>
            pHandle(e);
        });

     // <pre><code></code></pre> 回车时 特殊处理
        function codeHandle(e) {
            var $selectionElem = editor.selection.getSelectionContainerElem();
            if (!$selectionElem) {
                return;
            }
            
            var $parentElem = $selectionElem.parent();
            var selectionNodeName = $selectionElem.getNodeName();
            var parentNodeName = $parentElem.getNodeName();

            if (!editor.cmd.queryCommandSupported('insertHTML')) {
                // 必须原生支持 insertHTML 命令
                return;
            }


            var $hide = $selectionElem.parentUntil('HIDE');
            if (selectionNodeName == 'HIDE' || $hide != null){
                //软回车效果
                if (window.getSelection) {
                	var selection = window.getSelection(),
                    range = selection.getRangeAt(0),
                    br = document.createElement("br");
                    range.deleteContents();
                    range.insertNode(br);
                    range.setStartAfter(br);
                    range.setEndAfter(br);
                    range.collapse(false);
                    selection.removeAllRanges();
                    selection.addRange(range);
                    
                   
                }
                // 阻止默认行为, 防止回车换行
                e.preventDefault();
            }
           



            
            if (selectionNodeName != 'HIDE') {
                if (selectionNodeName !== 'CODE' || parentNodeName !== 'PRE') {
                    // 不符合要求 忽略
                    return;
                }
                // 处理：光标定位到代码末尾，联系点击两次回车，即跳出代码块
                if (editor._willBreakCode === true) {
                    // 此时可以跳出代码块
                    // 插入 <p> ，并将选取定位到 <p>
                    var $p = $('<p><br></p>');
                    $p.insertAfter($parentElem);
                    editor.selection.createRangeByElem($p, true);
                    editor.selection.restoreSelection();

                    // 修改状态
                    editor._willBreakCode = false;

                    e.preventDefault();
                    return;
                }

                var _startOffset = editor.selection.getRange().startOffset;

                
                
                // 处理：回车时，不能插入 <br> 而是插入 \n ，因为是在 pre 标签里面
                editor.cmd.do('insertHTML', '\n');
                editor.selection.saveRange();
                if (editor.selection.getRange().startOffset === _startOffset) {
                    // 没起作用，再来一遍
                    editor.cmd.do('insertHTML', '\n');
                }

                var codeLength = $selectionElem.html().length;
                if (editor.selection.getRange().startOffset + 1 === codeLength) {
                    // 说明光标在代码最后的位置，执行了回车操作
                    // 记录下来，以便下次回车时候跳出 code
                    editor._willBreakCode = true;
                }

                // 阻止默认行为, 防止回车换行
                e.preventDefault();
            }

            /**
            //处理隐藏标签
            if (selectionNodeName === 'HIDE') {

                //软回车效果
                if (window.getSelection) {
                	var selection = window.getSelection(),
                    range = selection.getRangeAt(0),
                    br = document.createElement("br");
                    range.deleteContents();
                    range.insertNode(br);
                    range.setStartAfter(br);
                    range.setEndAfter(br);
                    range.collapse(false);
                    selection.removeAllRanges();
                    selection.addRange(range);
                    
                   
                }
                // 阻止默认行为, 防止回车换行
                e.preventDefault();
            }else{
            	if (selectionNodeName !== 'CODE' || parentNodeName !== 'PRE') {
                    // 不符合要求 忽略
                    return;
                }
                // 处理：光标定位到代码末尾，联系点击两次回车，即跳出代码块
                if (editor._willBreakCode === true) {
                    // 此时可以跳出代码块
                    // 插入 <p> ，并将选取定位到 <p>
                    var $p = $('<p><br></p>');
                    $p.insertAfter($parentElem);
                    editor.selection.createRangeByElem($p, true);
                    editor.selection.restoreSelection();

                    // 修改状态
                    editor._willBreakCode = false;

                    e.preventDefault();
                    return;
                }

                var _startOffset = editor.selection.getRange().startOffset;

                
                
                // 处理：回车时，不能插入 <br> 而是插入 \n ，因为是在 pre 标签里面
                editor.cmd.do('insertHTML', '\n');
                editor.selection.saveRange();
                if (editor.selection.getRange().startOffset === _startOffset) {
                    // 没起作用，再来一遍
                    editor.cmd.do('insertHTML', '\n');
                }

                var codeLength = $selectionElem.html().length;
                if (editor.selection.getRange().startOffset + 1 === codeLength) {
                    // 说明光标在代码最后的位置，执行了回车操作
                    // 记录下来，以便下次回车时候跳出 code
                    editor._willBreakCode = true;
                }

                // 阻止默认行为, 防止回车换行
                e.preventDefault();
            	
            }**/
        }

        $textElem.on('keydown', function (e) {
            if (e.keyCode !== 13) {
                // 不是回车键
                // 取消即将跳转代码块的记录
                editor._willBreakCode = false;
                return;
            }
            // <pre><code></code></pre> 回车时 特殊处理
            codeHandle(e);
        });
    },

    // 清空时保留 <p><br></p>
    _clearHandle: function _clearHandle() {
        var editor = this.editor;
        var $textElem = editor.$textElem;

        $textElem.on('keydown', function (e) {
            if (e.keyCode !== 8) {
                return;
            }
            var $selectionElem = editor.selection.getSelectionContainerElem();

            //退格键删除隐藏标签
            if($selectionElem.getNodeName() == "HIDE"){
                //<hide class="inputValue_20" hide-type="20"></hide>
                if($selectionElem.children() != undefined && $selectionElem.children().length == undefined 
                        && $selectionElem.text() == ''){

                    $selectionElem.remove();
                    editor.selection.restoreSelection();
                    e.preventDefault();
                    return;
                }


                //<hide class="inputValue_20" hide-type="20"><br></hide>
                if($selectionElem.children() != undefined && $selectionElem.children().length == 1
                        && $selectionElem.text() == ''
                        && $selectionElem.children()[0].outerHTML == '<br>'){// $selectionElem.children()[0]为 HTMLBRElement类型数据    Object.prototype.toString.call($selectionElem.children()[0])
                   
                    $selectionElem.remove();
                    editor.selection.restoreSelection();
                    e.preventDefault();
                    return;
                }
            }
            


            //按下退格键
            var txtHtml = $textElem.html().toLowerCase().trim();
            if (txtHtml === '<p><br></p>') {
                // 最后剩下一个空行，就不再删除了
                e.preventDefault();
                return;
            }
        });

        $textElem.on('keyup', function (e) {
            if (e.keyCode !== 8) {
                return;
            }
            //释放退格键
            var $p = void 0;
            var txtHtml = $textElem.html().toLowerCase().trim();

            // firefox 时用 txtHtml === '<br>' 判断，其他用 !txtHtml 判断
            if (!txtHtml || txtHtml === '<br>') {
                // 内容空了
                $p = $('<p><br/></p>');
                $textElem.html(''); // 一定要先清空，否则在 firefox 下有问题
                $textElem.append($p);
                editor.selection.createRangeByElem($p, false, true);
                editor.selection.restoreSelection();
            }
        });
    },

    // 粘贴事件（粘贴文字 粘贴图片）
    _pasteHandle: function _pasteHandle() {
        var editor = this.editor;
        var config = editor.config;
        var pasteFilterStyle = config.pasteFilterStyle;
        var pasteTextHandle = config.pasteTextHandle;
        var ignoreImg = config.pasteIgnoreImg;
        var $textElem = editor.$textElem;

        // 粘贴图片、文本的事件，每次只能执行一个
        // 判断该次粘贴事件是否可以执行
        var pasteTime = 0;
        function canDo() {
            var now = Date.now();
            var flag = false;
            if (now - pasteTime >= 100) {
                // 间隔大于 100 ms ，可以执行
                flag = true;
            }
            pasteTime = now;
            return flag;
        }
        function resetTime() {
            pasteTime = 0;
        }

        // 粘贴文字
        $textElem.on('paste', function (e) {
            if (UA.isIE()) {
                return;
            } else {
                // 阻止默认行为，使用 execCommand 的粘贴命令
                e.preventDefault();
            }

            // 粘贴图片和文本，只能同时使用一个
            if (!canDo()) {
                return;
            }

            // 获取粘贴的文字
            var pasteHtml = getPasteHtml(e, pasteFilterStyle, ignoreImg);
            var pasteText = getPasteText(e);
            pasteText = pasteText.replace(/\n/gm, '<br>');

            var $selectionElem = editor.selection.getSelectionContainerElem();
            if (!$selectionElem) {
                return;
            }
            var nodeName = $selectionElem.getNodeName();

            // code 中只能粘贴纯文本
            if (nodeName === 'CODE' || nodeName === 'PRE') {
                if (pasteTextHandle && isFunction(pasteTextHandle)) {
                    // 用户自定义过滤处理粘贴内容
                    pasteText = '' + (pasteTextHandle(pasteText) || '');
                }
                editor.cmd.do('insertHTML', '<p>' + pasteText + '</p>');
                return;
            }

            // 先放开注释，有问题再追查 ————
            // // 表格中忽略，可能会出现异常问题
            // if (nodeName === 'TD' || nodeName === 'TH') {
            //     return
            // }

            if (!pasteHtml) {
                // 没有内容，可继续执行下面的图片粘贴
                resetTime();
                return;
            }
            try {
                // firefox 中，获取的 pasteHtml 可能是没有 <ul> 包裹的 <li>
                // 因此执行 insertHTML 会报错
                if (pasteTextHandle && isFunction(pasteTextHandle)) {
                    // 用户自定义过滤处理粘贴内容
                    pasteHtml = '' + (pasteTextHandle(pasteHtml) || '');
                }
                editor.cmd.do('insertHTML', pasteHtml);
            } catch (ex) {
                // 此时使用 pasteText 来兼容一下
                if (pasteTextHandle && isFunction(pasteTextHandle)) {
                    // 用户自定义过滤处理粘贴内容
                    pasteText = '' + (pasteTextHandle(pasteText) || '');
                }
                editor.cmd.do('insertHTML', '<p>' + pasteText + '</p>');
            }
        });

        // 粘贴图片
        $textElem.on('paste', function (e) {
            if (UA.isIE()) {
                return;
            } else {
                e.preventDefault();
            }

            // 粘贴图片和文本，只能同时使用一个
            if (!canDo()) {
                return;
            }

            // 获取粘贴的图片
            var pasteFiles = getPasteImgs(e);
            if (!pasteFiles || !pasteFiles.length) {
                return;
            }

            // 获取当前的元素
            var $selectionElem = editor.selection.getSelectionContainerElem();
            if (!$selectionElem) {
                return;
            }
            var nodeName = $selectionElem.getNodeName();

            // code 中粘贴忽略
            if (nodeName === 'CODE' || nodeName === 'PRE') {
                return;
            }

            // 上传图片
            var uploadImg = editor.uploadImg;
            uploadImg.uploadImg(pasteFiles);
        });
    },

    // tab 特殊处理
    _tabHandle: function _tabHandle() {
        var editor = this.editor;
        var $textElem = editor.$textElem;

        $textElem.on('keydown', function (e) {
            if (e.keyCode !== 9) {
                return;
            }
            if (!editor.cmd.queryCommandSupported('insertHTML')) {
                // 必须原生支持 insertHTML 命令
                return;
            }
            var $selectionElem = editor.selection.getSelectionContainerElem();
            if (!$selectionElem) {
                return;
            }
            var $parentElem = $selectionElem.parent();
            var selectionNodeName = $selectionElem.getNodeName();
            var parentNodeName = $parentElem.getNodeName();

            if (selectionNodeName === 'CODE' && parentNodeName === 'PRE') {
                // <pre><code> 里面
                editor.cmd.do('insertHTML', '    ');
            } else {
                // 普通文字
                editor.cmd.do('insertHTML', '&nbsp;&nbsp;&nbsp;&nbsp;');
            }

            e.preventDefault();
        });
    },

    // img 点击
    _imgHandle: function _imgHandle() {
        var editor = this.editor;
        var $textElem = editor.$textElem;

        // 为图片增加 selected 样式
        $textElem.on('click', 'img', function (e) {
            var img = this;
            var $img = $(img);

            if ($img.attr('data-w-e') === '1') {
                // 是表情图片，忽略
                return;
            }

            // 记录当前点击过的图片
            editor._selectedImg = $img;

            // 修改选区并 restore ，防止用户此时点击退格键，会删除其他内容
            editor.selection.createRangeByElem($img);
            editor.selection.restoreSelection();
        });

        // 去掉图片的 selected 样式
        $textElem.on('click  keyup', function (e) {
            if (e.target.matches('img')) {
                // 点击的是图片，忽略
                return;
            }
            // 删除记录
            editor._selectedImg = null;
        });
    },

    // 拖拽事件
    _dragHandle: function _dragHandle() {
        var editor = this.editor;

        // 禁用 document 拖拽事件
        var $document = $(document);
        $document.on('dragleave drop dragenter dragover', function (e) {
            e.preventDefault();
        });

        // 添加编辑区域拖拽事件
        var $textElem = editor.$textElem;
        $textElem.on('drop', function (e) {
            e.preventDefault();
            var files = e.dataTransfer && e.dataTransfer.files;
            if (!files || !files.length) {
                return;
            }

            // 上传图片
            var uploadImg = editor.uploadImg;
            uploadImg.uploadImg(files);
        });
    }
};

/*
    命令，封装 document.execCommand
*/

// 构造函数
function Command(editor) {
    this.editor = editor;
}

// 修改原型
Command.prototype = {
    constructor: Command,

    // 执行命令
    do: function _do(name, value) {
        var editor = this.editor;

        // 使用 styleWithCSS
        if (!editor._useStyleWithCSS) {
            document.execCommand('styleWithCSS', null, true);
            editor._useStyleWithCSS = true;
        }

        // 如果无选区，忽略
        if (!editor.selection.getRange()) {
            return;
        }

        // 恢复选取
        editor.selection.restoreSelection();
        
        // 执行
        var _name = '_' + name;
        if (this[_name]) {	
            // 有自定义事件
            this[_name](value);
        } else {
            // 默认 command
            this._execCommand(name, value);
        }
        
        // 修改菜单状态
        editor.menus.changeActive();

        // 最后，恢复选取保证光标在原来的位置闪烁
        editor.selection.saveRange();
        editor.selection.restoreSelection();

        // 触发 onchange
        editor.change && editor.change();
        
    },

    // 自定义 insertHTML 事件
    _insertHTML: function _insertHTML(html) {
    	
        var editor = this.editor;
        var range = editor.selection.getRange();

        if (this.queryCommandSupported('insertHTML')) {
            // W3C
            this._execCommand('insertHTML', html);
        } else if (range.insertNode) {
            // IE
            range.deleteContents();
            range.insertNode($(html)[0]);
        } else if (range.pasteHTML) {
            // IE <= 10
            range.pasteHTML(html);
        }
    },

    // 插入 elem
    _insertElem: function _insertElem($elem) {
        var editor = this.editor;
        var range = editor.selection.getRange();

        if (range.insertNode) {
            range.deleteContents();
            range.insertNode($elem[0]);
        }
    },

    // 封装 execCommand (夸克浏览器Web537.36 Chrome 57.0.2987.108)不支持本函数
    _execCommand: function _execCommand(name, value) {
        document.execCommand(name, false, value);
    },

    // 封装 document.queryCommandValue
    queryCommandValue: function queryCommandValue(name) {
        return document.queryCommandValue(name);
    },

    // 封装 document.queryCommandState
    queryCommandState: function queryCommandState(name) {
        return document.queryCommandState(name);
    },

    // 封装 document.queryCommandSupported
    queryCommandSupported: function queryCommandSupported(name) {
        return document.queryCommandSupported(name);
    }
};

/*
    selection range API
*/

// 构造函数
function API(editor) {
    this.editor = editor;
    this._currentRange = null;
}

// 修改原型
API.prototype = {
    constructor: API,

    // 获取 range 对象
    getRange: function getRange() {
        return this._currentRange;
    },

    // 保存选区
    saveRange: function saveRange(_range) {
        if (_range) {
            // 保存已有选区
            this._currentRange = _range;
            return;
        }

        // 获取当前的选区
        var selection = window.getSelection();
        if (selection.rangeCount === 0) {
            return;
        }
        var range = selection.getRangeAt(0);

        // 判断选区内容是否在编辑内容之内
        var $containerElem = this.getSelectionContainerElem(range);
        if (!$containerElem) {
            return;
        }

        // 判断选区内容是否在不可编辑区域之内
        if ($containerElem.attr('contenteditable') === 'false' || $containerElem.parentUntil('[contenteditable=false]')) {
            return;
        }

        var editor = this.editor;
        var $textElem = editor.$textElem;
        if ($textElem.isContain($containerElem)) {
            // 是编辑内容之内的
            this._currentRange = range;
        }
    },

    // 折叠选区
    collapseRange: function collapseRange(toStart) {
        if (toStart == null) {
            // 默认为 false
            toStart = false;
        }
        var range = this._currentRange;
        if (range) {
            range.collapse(toStart);
        }
    },

    // 选中区域的文字
    getSelectionText: function getSelectionText() {
        var range = this._currentRange;
        if (range) {
            return this._currentRange.toString();
        } else {
            return '';
        }
    },

    // 选区的 $Elem
    getSelectionContainerElem: function getSelectionContainerElem(range) {
        range = range || this._currentRange;
        var elem = void 0;
        if (range) {
            elem = range.commonAncestorContainer;
            return $(elem.nodeType === 1 ? elem : elem.parentNode);
        }
    },
    getSelectionStartElem: function getSelectionStartElem(range) {
        range = range || this._currentRange;
        var elem = void 0;
        if (range) {
            elem = range.startContainer;
            return $(elem.nodeType === 1 ? elem : elem.parentNode);
        }
    },
    getSelectionEndElem: function getSelectionEndElem(range) {
        range = range || this._currentRange;
        var elem = void 0;
        if (range) {
            elem = range.endContainer;
            return $(elem.nodeType === 1 ? elem : elem.parentNode);
        }
    },

    // 选区是否为空
    isSelectionEmpty: function isSelectionEmpty() {
        var range = this._currentRange;
        if (range && range.startContainer) {
            if (range.startContainer === range.endContainer) {
                if (range.startOffset === range.endOffset) {
                    return true;
                }
            }
        }
        return false;
    },

    // 恢复选区
    restoreSelection: function restoreSelection() {
        var selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(this._currentRange);
    },

    // 创建一个空白（即 &#8203 字符）选区
    createEmptyRange: function createEmptyRange() {
        var editor = this.editor;
        var range = this.getRange();
        var $elem = void 0;

        if (!range) {
            // 当前无 range
            return;
        }
        if (!this.isSelectionEmpty()) {
            // 当前选区必须没有内容才可以
            return;
        }

        try {
            // 目前只支持 webkit 内核
            if (UA.isWebkit()) {
                // 插入 &#8203
                editor.cmd.do('insertHTML', '&#8203;');
                // 修改 offset 位置
                range.setEnd(range.endContainer, range.endOffset + 1);
                // 存储
                this.saveRange(range);
            } else {
                $elem = $('<strong>&#8203;</strong>');
                editor.cmd.do('insertElem', $elem);
                this.createRangeByElem($elem, true);
            }
        } catch (ex) {
            // 部分情况下会报错，兼容一下
        }
    },

    // 根据 $Elem 设置选区
    createRangeByElem: function createRangeByElem($elem, toStart, isContent) {
        // $elem - 经过封装的 elem
        // toStart - true 开始位置，false 结束位置
        // isContent - 是否选中Elem的内容
        if (!$elem.length) {
            return;
        }

        var elem = $elem[0];
        var range = document.createRange();

        if (isContent) {
            range.selectNodeContents(elem);
        } else {
            range.selectNode(elem);
        }

        if (typeof toStart === 'boolean') {
            range.collapse(toStart);
        }

        // 存储 range
        this.saveRange(range);
    }
};

/*
    上传进度条
*/

function Progress(editor) {
    this.editor = editor;
    this._time = 0;
    this._isShow = false;
    this._isRender = false;
    this._timeoutId = 0;
    this.$textContainer = editor.$textContainerElem;
    this.$bar = $('<div class="w-e-progress"></div>');
}

Progress.prototype = {
    constructor: Progress,

    show: function show(progress) {
        var _this = this;

        // 状态处理
        if (this._isShow) {
            return;
        }
        this._isShow = true;

        // 渲染
        var $bar = this.$bar;
        if (!this._isRender) {
            var $textContainer = this.$textContainer;
            $textContainer.append($bar);
        } else {
            this._isRender = true;
        }

        // 改变进度（节流，100ms 渲染一次）
        if (Date.now() - this._time > 100) {
            if (progress <= 1) {
                $bar.css('width', progress * 100 + '%');
                this._time = Date.now();
            }
        }

        // 隐藏
        var timeoutId = this._timeoutId;
        if (timeoutId) {
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(function () {
            _this._hide();
        }, 500);
    },

    _hide: function _hide() {
        var $bar = this.$bar;
        $bar.remove();

        // 修改状态
        this._time = 0;
        this._isShow = false;
        this._isRender = false;
    }
};

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) {
  return typeof obj;
} : function (obj) {
  return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
};

/*
    上传图片
*/

// 构造函数
function UploadImg(editor) {
    this.editor = editor;
}

// 原型
UploadImg.prototype = {
    constructor: UploadImg,

    // 根据 debug 弹出不同的信息
    _alert: function _alert(alertInfo, debugInfo) {
        var editor = this.editor;
        var debug = editor.config.debug;
        var customAlert = editor.config.customAlert;

        if (debug) {
            throw new Error('wangEditor: ' + (debugInfo || alertInfo));
        } else {
            if (customAlert && typeof customAlert === 'function') {
                customAlert(alertInfo);
            } else {
                alert(alertInfo);
            }
        }
    },

    // 根据链接插入图片
    insertLinkImg: function insertLinkImg(link) {
        var _this2 = this;

        if (!link) {
            return;
        }
        var editor = this.editor;
        var config = editor.config;

        // 校验格式
        var linkImgCheck = config.linkImgCheck;
        var checkResult = void 0;
        if (linkImgCheck && typeof linkImgCheck === 'function') {
            checkResult = linkImgCheck(link);
            if (typeof checkResult === 'string') {
                // 校验失败，提示信息
                alert(checkResult);
                return;
            }
        }

        editor.cmd.do('insertHTML', '<img src="' + link + '" style="max-width:100%;"/>');

        // 验证图片 url 是否有效，无效的话给出提示
        var img = document.createElement('img');
        img.onload = function () {
            var callback = config.linkImgCallback;
            if (callback && typeof callback === 'function') {
                callback(link);
            }

            img = null;
        };
        img.onerror = function () {
            img = null;
            // 无法成功下载图片
            _this2._alert('插入图片错误', 'wangEditor: \u63D2\u5165\u56FE\u7247\u51FA\u9519\uFF0C\u56FE\u7247\u94FE\u63A5\u662F "' + link + '"\uFF0C\u4E0B\u8F7D\u8BE5\u94FE\u63A5\u5931\u8D25');
            return;
        };
        img.onabort = function () {
            img = null;
        };
        img.src = link;
    },

    // 上传图片
    uploadImg: function uploadImg(files) {
        var _this3 = this;

        if (!files || !files.length) {
            return;
        }

        // ------------------------------ 获取配置信息 ------------------------------
        var editor = this.editor;
        var config = editor.config;
        var uploadImgServer = config.uploadImgServer;
        var uploadImgShowBase64 = config.uploadImgShowBase64;

        var maxSize = config.uploadImgMaxSize;
        var maxSizeM = maxSize / 1024 / 1024;
        var maxLength = config.uploadImgMaxLength || 10000;
        var uploadFileName = config.uploadFileName || '';
        var uploadImgParams = config.uploadImgParams || {};
        var uploadImgParamsWithUrl = config.uploadImgParamsWithUrl;
        var uploadImgHeaders = config.uploadImgHeaders || {};
        var hooks = config.uploadImgHooks || {};
        var timeout = config.uploadImgTimeout || 3000;
        var withCredentials = config.withCredentials;
        if (withCredentials == null) {
            withCredentials = false;
        }
        var customUploadImg = config.customUploadImg;

        if (!customUploadImg) {
            // 没有 customUploadImg 的情况下，需要如下两个配置才能继续进行图片上传
            if (!uploadImgServer && !uploadImgShowBase64) {
                return;
            }
        }

        // ------------------------------ 验证文件信息 ------------------------------
        var resultFiles = [];
        var errInfo = [];
        arrForEach(files, function (file) {
            var name = file.name;
            var size = file.size;

            // chrome 低版本 name === undefined
            if (!name || !size) {
                return;
            }

            if (/\.(jpg|jpeg|png|bmp|gif|webp)$/i.test(name) === false) {
                // 后缀名不合法，不是图片
                errInfo.push('\u3010' + name + '\u3011\u4E0D\u662F\u56FE\u7247');
                return;
            }
            if (maxSize < size) {
                // 上传图片过大
                errInfo.push('\u3010' + name + '\u3011\u5927\u4E8E ' + maxSizeM + 'M');
                return;
            }

            // 验证通过的加入结果列表
            resultFiles.push(file);
        });
        // 抛出验证信息
        if (errInfo.length) {
            this._alert('图片验证未通过: \n' + errInfo.join('\n'));
            return;
        }
        if (resultFiles.length > maxLength) {
            this._alert('一次最多上传' + maxLength + '张图片');
            return;
        }

        // ------------------------------ 自定义上传 ------------------------------
        if (customUploadImg && typeof customUploadImg === 'function') {
            customUploadImg(resultFiles, this.insertLinkImg.bind(this));

            // 阻止以下代码执行
            return;
        }

        // 添加图片数据
        var formdata = new FormData();
        arrForEach(resultFiles, function (file) {
            var name = uploadFileName || file.name;
            formdata.append(name, file);
        });

        // ------------------------------ 上传图片 ------------------------------
        if (uploadImgServer && typeof uploadImgServer === 'string') {
            // 添加参数
            var uploadImgServerArr = uploadImgServer.split('#');
            uploadImgServer = uploadImgServerArr[0];
            var uploadImgServerHash = uploadImgServerArr[1] || '';
            objForEach(uploadImgParams, function (key, val) {
                // 因使用者反应，自定义参数不能默认 encode ，由 v3.1.1 版本开始注释掉
                // val = encodeURIComponent(val)

                // 第一，将参数拼接到 url 中
                if (uploadImgParamsWithUrl) {
                    if (uploadImgServer.indexOf('?') > 0) {
                        uploadImgServer += '&';
                    } else {
                        uploadImgServer += '?';
                    }
                    uploadImgServer = uploadImgServer + key + '=' + val;
                }

                // 第二，将参数添加到 formdata 中
                formdata.append(key, val);
            });
            if (uploadImgServerHash) {
                uploadImgServer += '#' + uploadImgServerHash;
            }

            // 定义 xhr
            var xhr = new XMLHttpRequest();
            xhr.open('POST', uploadImgServer);

            // 设置超时
            xhr.timeout = timeout;
            xhr.ontimeout = function () {
                // hook - timeout
                if (hooks.timeout && typeof hooks.timeout === 'function') {
                    hooks.timeout(xhr, editor);
                }

                _this3._alert('上传图片超时');
            };

            // 监控 progress
            if (xhr.upload) {
                xhr.upload.onprogress = function (e) {
                    var percent = void 0;
                    // 进度条
                    var progressBar = new Progress(editor);
                    if (e.lengthComputable) {
                        percent = e.loaded / e.total;
                        progressBar.show(percent);
                    }
                };
            }

            // 返回数据
            xhr.onreadystatechange = function () {
                var result = void 0;
                if (xhr.readyState === 4) {
                    if (xhr.status < 200 || xhr.status >= 300) {
                        // hook - error
                        if (hooks.error && typeof hooks.error === 'function') {
                            hooks.error(xhr, editor);
                        }

                        // xhr 返回状态错误
                        _this3._alert('上传图片发生错误', '\u4E0A\u4F20\u56FE\u7247\u53D1\u751F\u9519\u8BEF\uFF0C\u670D\u52A1\u5668\u8FD4\u56DE\u72B6\u6001\u662F ' + xhr.status);
                        return;
                    }

                    result = xhr.responseText;
                    if ((typeof result === 'undefined' ? 'undefined' : _typeof(result)) !== 'object') {
                        try {
                            result = JSON.parse(result);
                        } catch (ex) {
                            // hook - fail
                            if (hooks.fail && typeof hooks.fail === 'function') {
                                hooks.fail(xhr, editor, result);
                            }

                            _this3._alert('上传图片失败', '上传图片返回结果错误，返回结果是: ' + result);
                            return;
                        }
                    }
                    if (!hooks.customInsert && result.errno != '0') {
                        // hook - fail
                        if (hooks.fail && typeof hooks.fail === 'function') {
                            hooks.fail(xhr, editor, result);
                        }

                        // 数据错误
                        _this3._alert('上传图片失败', '上传图片返回结果错误，返回结果 errno=' + result.errno);
                    } else {
                        if (hooks.customInsert && typeof hooks.customInsert === 'function') {
                            // 使用者自定义插入方法
                            hooks.customInsert(_this3.insertLinkImg.bind(_this3), result, editor);
                        } else {
                            // 将图片插入编辑器
                            var data = result.data || [];
                            data.forEach(function (link) {
                                _this3.insertLinkImg(link);
                            });
                        }

                        // hook - success
                        if (hooks.success && typeof hooks.success === 'function') {
                            hooks.success(xhr, editor, result);
                        }
                    }
                }
            };

            // hook - before
            if (hooks.before && typeof hooks.before === 'function') {
                var beforeResult = hooks.before(xhr, editor, resultFiles);
                if (beforeResult && (typeof beforeResult === 'undefined' ? 'undefined' : _typeof(beforeResult)) === 'object') {
                    if (beforeResult.prevent) {
                        // 如果返回的结果是 {prevent: true, msg: 'xxxx'} 则表示用户放弃上传
                        this._alert(beforeResult.msg);
                        return;
                    }
                }
            }

            // 自定义 headers
            objForEach(uploadImgHeaders, function (key, val) {
                xhr.setRequestHeader(key, val);
            });

            // 跨域传 cookie
            xhr.withCredentials = withCredentials;

            // 发送请求
            xhr.send(formdata);

            // 注意，要 return 。不去操作接下来的 base64 显示方式
            return;
        }

        // ------------------------------ 显示 base64 格式 ------------------------------
        if (uploadImgShowBase64) {
            arrForEach(files, function (file) {
                var _this = _this3;
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function () {
                    _this.insertLinkImg(this.result);
                };
            });
        }
    }
};


/*
上传文件
*/

//构造函数
function UploadFile(editor) {
this.editor = editor;
this.inputFileDescriptionId = null;
this.inputFileId = null;
}

//原型
UploadFile.prototype = {
constructor: UploadFile,

// 根据 debug 弹出不同的信息
_alert: function _alert(alertInfo, debugInfo) {
    var editor = this.editor;
    var debug = editor.config.debug;
    var customAlert = editor.config.customAlert;

    if (debug) {
        throw new Error('wangEditor: ' + (debugInfo || alertInfo));
    } else {
        if (customAlert && typeof customAlert === 'function') {
            customAlert(alertInfo);
        } else {
            alert(alertInfo);
        }
    }
},

// 将链接回填至input框
writeLinkToInput: function writeLinkToInput(link,text) {
    if (!link) {
        return;
    }
    
    var inputFile = document.getElementById(this.inputFileId);
    inputFile.value = link;
    
    var inputFileDescription = document.getElementById(this.inputFileDescriptionId);
    inputFileDescription.value = text;
    
   
    // const editor = this.editor
    // const config = editor.config

    // // 校验格式
    // const linkImgCheck = config.linkImgCheck
    // let checkResult
    // if (linkImgCheck && typeof linkImgCheck === 'function') {
    //     checkResult = linkImgCheck(link)
    //     if (typeof checkResult === 'string') {
    //         // 校验失败，提示信息
    //         alert(checkResult)
    //         return
    //     }
    // }

    // editor.cmd.do('insertHTML', `<img src="${link}" style="max-width:100%;"/>`)
},

// 上传文件
uploadFile: function uploadFile(files, inputFileId,inputFileDescriptionId) {
    if (!files || !files.length) {
        return;
    }
    
    // ------------------------------ 获取配置信息 ------------------------------
    var editor = this.editor;
    var config = editor.config;
    var customUploadFile = config.customUploadFile;
    this.inputFileId = inputFileId;
    this.inputFileDescriptionId = inputFileDescriptionId;
    if (!customUploadFile) {
        this._alert('请配置customUploadFile参数!');
        return;
    }
   
    // ------------------------------ 自定义上传 ------------------------------
    if (customUploadFile && typeof customUploadFile === 'function') {
        customUploadFile(files, this.writeLinkToInput.bind(this));

        // 阻止以下代码执行
        return;
    }
}
};

/*
    编辑器构造函数
*/

// id，累加
var editorId = 1;

// 构造函数
function Editor(toolbarSelector, textSelector) {
    if (toolbarSelector == null) {
        // 没有传入任何参数，报错
        throw new Error('错误：初始化编辑器时候未传入任何参数，请查阅文档');
    }
    // id，用以区分单个页面不同的编辑器对象
    this.id = 'wangEditor-' + editorId++;

    this.toolbarSelector = toolbarSelector;
    this.textSelector = textSelector;

    // 自定义配置
    this.customConfig = {};
}

// 修改原型
Editor.prototype = {
    constructor: Editor,

    // 初始化配置
    _initConfig: function _initConfig() {
        // _config 是默认配置，this.customConfig 是用户自定义配置，将它们 merge 之后再赋值
        var target = {};
        this.config = Object.assign(target, config, this.customConfig);

        // 将语言配置，生成正则表达式
        var langConfig = this.config.lang || {};
        var langArgs = [];
        objForEach(langConfig, function (key, val) {
            // key 即需要生成正则表达式的规则，如“插入链接”
            // val 即需要被替换成的语言，如“insert link”
            langArgs.push({
                reg: new RegExp(key, 'img'),
                val: val

            });
        });
        this.config.langArgs = langArgs;
    },

    // 初始化 DOM
    _initDom: function _initDom() {
        var _this = this;

        var toolbarSelector = this.toolbarSelector;
        var $toolbarSelector = $(toolbarSelector);
        var textSelector = this.textSelector;

        var config$$1 = this.config;
        var zIndex = config$$1.zIndex;

        // 定义变量
        var $toolbarElem = void 0,
            $textContainerElem = void 0,
            $textElem = void 0,
            $children = void 0;

        if (textSelector == null) {
            // 只传入一个参数，即是容器的选择器或元素，toolbar 和 text 的元素自行创建
            $toolbarElem = $('<div></div>');
            $textContainerElem = $('<div></div>');

            // 将编辑器区域原有的内容，暂存起来
            $children = $toolbarSelector.children();

            // 添加到 DOM 结构中
            $toolbarSelector.append($toolbarElem).append($textContainerElem);

            // 自行创建的，需要配置默认的样式
            $toolbarElem.css('background-color', '#f1f1f1').css('border', '1px solid #ccc');
            $textContainerElem.css('border', '1px solid #ccc').css('border-top', 'none').css('height', '300px');
        } else {
            // toolbar 和 text 的选择器都有值，记录属性
            $toolbarElem = $toolbarSelector;
            $textContainerElem = $(textSelector);
            // 将编辑器区域原有的内容，暂存起来
            $children = $textContainerElem.children();
        }

        // 编辑区域
        $textElem = $('<div></div>');
        $textElem.attr('contenteditable', 'true').css('width', '100%').css('height', '100%');

        // 初始化编辑区域内容
        if ($children && $children.length) {
            $textElem.append($children);
        } else {
            $textElem.append($('<p><br></p>'));
        }

        // 编辑区域加入DOM
        $textContainerElem.append($textElem);

        // 设置通用的 class
        $toolbarElem.addClass('w-e-toolbar');
        $textContainerElem.addClass('w-e-text-container');
        $textContainerElem.css('z-index', zIndex);
        $textElem.addClass('w-e-text');

        // 添加 ID
        var toolbarElemId = getRandom('toolbar-elem');
        $toolbarElem.attr('id', toolbarElemId);
        var textElemId = getRandom('text-elem');
        $textElem.attr('id', textElemId);

        // 记录属性
        this.$toolbarElem = $toolbarElem;
        this.$textContainerElem = $textContainerElem;
        this.$textElem = $textElem;
        this.toolbarElemId = toolbarElemId;
        this.textElemId = textElemId;

        // 记录输入法的开始和结束
        var compositionEnd = true;
        $textContainerElem.on('compositionstart', function () {
            // 输入法开始输入
            compositionEnd = false;
        });
        $textContainerElem.on('compositionend', function () {
            // 输入法结束输入
            compositionEnd = true;
        });

        // 绑定 onchange
        $textContainerElem.on('click keyup', function () {
            // 输入法结束才出发 onchange
            compositionEnd && _this.change && _this.change();
        });
        $toolbarElem.on('click', function () {
            this.change && this.change();
        });

        //绑定 onfocus 与 onblur 事件
        if (config$$1.onfocus || config$$1.onblur) {
            // 当前编辑器是否是焦点状态
            this.isFocus = false;

            $(document).on('click', function (e) {
                //判断当前点击元素是否在编辑器内
                var isChild = $textElem.isContain($(e.target));

                //判断当前点击元素是否为工具栏
                var isToolbar = $toolbarElem.isContain($(e.target));
                var isMenu = $toolbarElem[0] == e.target ? true : false;

                if (!isChild) {
                    //若为选择工具栏中的功能，则不视为成blur操作
                    if (isToolbar && !isMenu) {
                        return;
                    }

                    if (_this.isFocus) {
                        _this.onblur && _this.onblur();
                    }
                    _this.isFocus = false;
                } else {
                    if (!_this.isFocus) {
                        _this.onfocus && _this.onfocus();
                    }
                    _this.isFocus = true;
                }
            });
        }
    },

    // 封装 command
    _initCommand: function _initCommand() {
        this.cmd = new Command(this);
    },

    // 封装 selection range API
    _initSelectionAPI: function _initSelectionAPI() {
        this.selection = new API(this);
    },

    // 添加图片上传
    _initUploadImg: function _initUploadImg() {
        this.uploadImg = new UploadImg(this);
    },
    
    // 添加文件上传
    _initUploadFile: function _initUploadFile() {
        this.uploadFile = new UploadFile(this);
    },


    // 初始化菜单
    _initMenus: function _initMenus() {
        this.menus = new Menus(this);
        this.menus.init();
    },

    // 添加 text 区域
    _initText: function _initText() {
        this.txt = new Text(this);
        this.txt.init();
    },

    // 初始化选区，将光标定位到内容尾部
    initSelection: function initSelection(newLine) {
        var $textElem = this.$textElem;
        var $children = $textElem.children();
        if (!$children.length) {
            // 如果编辑器区域无内容，添加一个空行，重新设置选区
            $textElem.append($('<p><br></p>'));
            this.initSelection();
            return;
        }

        var $last = $children.last();

        if (newLine) {
            // 新增一个空行
            var html = $last.html().toLowerCase();
            var nodeName = $last.getNodeName();
            if (html !== '<br>' && html !== '<br\/>' || nodeName !== 'P') {
                // 最后一个元素不是 <p><br></p>，添加一个空行，重新设置选区
                $textElem.append($('<p><br></p>'));
                this.initSelection();
                return;
            }
        }

        this.selection.createRangeByElem($last, false, true);
        this.selection.restoreSelection();
    },

    // 绑定事件
    _bindEvent: function _bindEvent() {
        // -------- 绑定 onchange 事件 --------
        var onChangeTimeoutId = 0;
        var beforeChangeHtml = this.txt.html();
        var config$$1 = this.config;

        // onchange 触发延迟时间
        var onchangeTimeout = config$$1.onchangeTimeout;
        onchangeTimeout = parseInt(onchangeTimeout, 10);
        if (!onchangeTimeout || onchangeTimeout <= 0) {
            onchangeTimeout = 200;
        }

        var onchange = config$$1.onchange;
        if (onchange && typeof onchange === 'function') {
            // 触发 change 的有三个场景：
            // 1. $textContainerElem.on('click keyup')
            // 2. $toolbarElem.on('click')
            // 3. editor.cmd.do()
            this.change = function () {
                // 判断是否有变化
                var currentHtml = this.txt.html();

                if (currentHtml.length === beforeChangeHtml.length) {
                    // 需要比较每一个字符
                    if (currentHtml === beforeChangeHtml) {
                        return;
                    }
                }

                // 执行，使用节流
                if (onChangeTimeoutId) {
                    clearTimeout(onChangeTimeoutId);
                }
                onChangeTimeoutId = setTimeout(function () {
                    // 触发配置的 onchange 函数
                    onchange(currentHtml);
                    beforeChangeHtml = currentHtml;
                }, onchangeTimeout);
            };
        }

        // -------- 绑定 onblur 事件 --------
        var onblur = config$$1.onblur;
        if (onblur && typeof onblur === 'function') {
            this.onblur = function () {
                var currentHtml = this.txt.html();
                onblur(currentHtml);
            };
        }

        // -------- 绑定 onfocus 事件 --------
        var onfocus = config$$1.onfocus;
        if (onfocus && typeof onfocus === 'function') {
            this.onfocus = function () {
                onfocus();
            };
        }
    },

    // 创建编辑器
    create: function create() {
        // 初始化配置信息
        this._initConfig();

        // 初始化 DOM
        this._initDom();

        // 封装 command API
        this._initCommand();

        // 封装 selection range API
        this._initSelectionAPI();

        // 添加 text
        this._initText();

        // 初始化菜单
        this._initMenus();

        // 添加 图片上传
        this._initUploadImg();
        
        // 添加 文件上传
        this._initUploadFile();

        // 初始化选区，将光标定位到内容尾部
        this.initSelection(true);

        // 绑定事件
        this._bindEvent();
    },

    // 解绑所有事件（暂时不对外开放）
    _offAllEvent: function _offAllEvent() {
        $.offAll();
    }
};

// 检验是否浏览器环境
try {
    document;
} catch (ex) {
    throw new Error('请在浏览器环境下运行');
}

// polyfill
polyfill();

// 这里的 `inlinecss` 将被替换成 css 代码的内容，详情可去 ./gulpfile.js 中搜索 `inlinecss` 关键字
//var inlinecss = '.w-e-toolbar,.w-e-text-container,.w-e-menu-panel {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-toolbar *,.w-e-text-container *,.w-e-menu-panel * {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-clear-fix:after {  content: "";  display: table;  clear: both;}.w-e-toolbar .w-e-droplist {  position: absolute;  left: 0;  top: 0;  background-color: #fff;  border: 1px solid #f1f1f1;  border-right-color: #ccc;  border-bottom-color: #ccc;}.w-e-toolbar .w-e-droplist .w-e-dp-title {  text-align: center;  color: #999;  line-height: 2;  border-bottom: 1px solid #f1f1f1;  font-size: 13px;}.w-e-toolbar .w-e-droplist ul.w-e-list {  list-style: none;  line-height: 1;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item {  color: #333;  padding: 5px 0;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item:hover {  background-color: #f1f1f1;}.w-e-toolbar .w-e-droplist ul.w-e-block {  list-style: none;  text-align: left;  padding: 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item {  display: inline-block;  *display: inline;  *zoom: 1;  padding: 3px 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item:hover {  background-color: #f1f1f1;}@font-face {  font-family: \'w-e-icon\';  src: url(data:application/x-font-woff;charset=utf-8;base64,d09GRgABAAAAABhQAAsAAAAAGAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABPUy8yAAABCAAAAGAAAABgDxIPBGNtYXAAAAFoAAABBAAAAQQrSf4BZ2FzcAAAAmwAAAAIAAAACAAAABBnbHlmAAACdAAAEvAAABLwfpUWUWhlYWQAABVkAAAANgAAADYQp00kaGhlYQAAFZwAAAAkAAAAJAfEA+FobXR4AAAVwAAAAIQAAACEeAcD7GxvY2EAABZEAAAARAAAAERBSEX+bWF4cAAAFogAAAAgAAAAIAAsALZuYW1lAAAWqAAAAYYAAAGGmUoJ+3Bvc3QAABgwAAAAIAAAACAAAwAAAAMD3gGQAAUAAAKZAswAAACPApkCzAAAAesAMwEJAAAAAAAAAAAAAAAAAAAAARAAAAAAAAAAAAAAAAAAAAAAQAAA8fwDwP/AAEADwABAAAAAAQAAAAAAAAAAAAAAIAAAAAAAAwAAAAMAAAAcAAEAAwAAABwAAwABAAAAHAAEAOgAAAA2ACAABAAWAAEAIOkG6Q3pEulH6Wbpd+m56bvpxunL6d/qDepc6l/qZepo6nHqefAN8BTxIPHc8fz//f//AAAAAAAg6QbpDekS6UfpZel36bnpu+nG6cvp3+oN6lzqX+pi6mjqcep38A3wFPEg8dzx/P/9//8AAf/jFv4W+Bb0FsAWoxaTFlIWURZHFkMWMBYDFbUVsxWxFa8VpxWiEA8QCQ7+DkMOJAADAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAB//8ADwABAAAAAAAAAAAAAgAANzkBAAAAAAEAAAAAAAAAAAACAAA3OQEAAAAAAQAAAAAAAAAAAAIAADc5AQAAAAACAAD/wAQAA8AABAATAAABNwEnAQMuAScTNwEjAQMlATUBBwGAgAHAQP5Anxc7MmOAAYDA/oDAAoABgP6ATgFAQAHAQP5A/p0yOxcBEU4BgP6A/YDAAYDA/oCAAAQAAAAABAADgAAQACEALQA0AAABOAExETgBMSE4ATEROAExITUhIgYVERQWMyEyNjURNCYjBxQGIyImNTQ2MzIWEyE1EwEzNwPA/IADgPyAGiYmGgOAGiYmGoA4KCg4OCgoOED9AOABAEDgA0D9AAMAQCYa/QAaJiYaAwAaJuAoODgoKDg4/biAAYD+wMAAAAIAAABABAADQAA4ADwAAAEmJy4BJyYjIgcOAQcGBwYHDgEHBhUUFx4BFxYXFhceARcWMzI3PgE3Njc2Nz4BNzY1NCcuAScmJwERDQED1TY4OXY8PT8/PTx2OTg2CwcICwMDAwMLCAcLNjg5djw9Pz89PHY5ODYLBwgLAwMDAwsIBwv9qwFA/sADIAgGBggCAgICCAYGCCkqKlktLi8vLi1ZKiopCAYGCAICAgIIBgYIKSoqWS0uLy8uLVkqKin94AGAwMAAAAAAAgDA/8ADQAPAABsAJwAAASIHDgEHBhUUFx4BFxYxMDc+ATc2NTQnLgEnJgMiJjU0NjMyFhUUBgIAQjs6VxkZMjJ4MjIyMngyMhkZVzo7QlBwcFBQcHADwBkZVzo7Qnh9fcxBQUFBzH19eEI7OlcZGf4AcFBQcHBQUHAAAAEAAAAABAADgAArAAABIgcOAQcGBycRISc+ATMyFx4BFxYVFAcOAQcGBxc2Nz4BNzY1NCcuAScmIwIANTIyXCkpI5YBgJA1i1BQRUZpHh4JCSIYGB5VKCAgLQwMKCiLXl1qA4AKCycbHCOW/oCQNDweHmlGRVArKClJICEaYCMrK2I2NjlqXV6LKCgAAQAAAAAEAAOAACoAABMUFx4BFxYXNyYnLgEnJjU0Nz4BNzYzMhYXByERByYnLgEnJiMiBw4BBwYADAwtICAoVR4YGCIJCR4eaUZFUFCLNZABgJYjKSlcMjI1al1eiygoAYA5NjZiKysjYBohIEkpKCtQRUZpHh48NJABgJYjHBsnCwooKIteXQAAAAACAAAAQAQBAwAAJgBNAAATMhceARcWFRQHDgEHBiMiJy4BJyY1JzQ3PgE3NjMVIgYHDgEHPgEhMhceARcWFRQHDgEHBiMiJy4BJyY1JzQ3PgE3NjMVIgYHDgEHPgHhLikpPRESEhE9KSkuLikpPRESASMjelJRXUB1LQkQBwgSAkkuKSk9ERISET0pKS4uKSk9ERIBIyN6UlFdQHUtCRAHCBICABIRPSkpLi4pKT0REhIRPSkpLiBdUVJ6IyOAMC4IEwoCARIRPSkpLi4pKT0REhIRPSkpLiBdUVJ6IyOAMC4IEwoCAQAABgBA/8AEAAPAAAMABwALABEAHQApAAAlIRUhESEVIREhFSEnESM1IzUTFTMVIzU3NSM1MxUVESM1MzUjNTM1IzUBgAKA/YACgP2AAoD9gMBAQECAwICAwMCAgICAgIACAIACAIDA/wDAQP3yMkCSPDJAku7+wEBAQEBAAAYAAP/ABAADwAADAAcACwAXACMALwAAASEVIREhFSERIRUhATQ2MzIWFRQGIyImETQ2MzIWFRQGIyImETQ2MzIWFRQGIyImAYACgP2AAoD9gAKA/YD+gEs1NUtLNTVLSzU1S0s1NUtLNTVLSzU1SwOAgP8AgP8AgANANUtLNTVLS/61NUtLNTVLS/61NUtLNTVLSwADAAAAAAQAA6AAAwANABQAADchFSElFSE1EyEVITUhJQkBIxEjEQAEAPwABAD8AIABAAEAAQD9YAEgASDggEBAwEBAAQCAgMABIP7g/wABAAAAAAACAB7/zAPiA7QAMwBkAAABIiYnJicmNDc2PwE+ATMyFhcWFxYUBwYPAQYiJyY0PwE2NCcuASMiBg8BBhQXFhQHDgEjAyImJyYnJjQ3Nj8BNjIXFhQPAQYUFx4BMzI2PwE2NCcmNDc2MhcWFxYUBwYPAQ4BIwG4ChMIIxISEhIjwCNZMTFZIyMSEhISI1gPLA8PD1gpKRQzHBwzFMApKQ8PCBMKuDFZIyMSEhISI1gPLA8PD1gpKRQzHBwzFMApKQ8PDysQIxISEhIjwCNZMQFECAckLS1eLS0kwCIlJSIkLS1eLS0kVxAQDysPWCl0KRQVFRTAKXQpDysQBwj+iCUiJC0tXi0tJFcQEA8rD1gpdCkUFRUUwCl0KQ8rEA8PJC0tXi0tJMAiJQAAAAAFAAD/wAQAA8AAGwA3AFMAXwBrAAAFMjc+ATc2NTQnLgEnJiMiBw4BBwYVFBceARcWEzIXHgEXFhUUBw4BBwYjIicuAScmNTQ3PgE3NhMyNz4BNzY3BgcOAQcGIyInLgEnJicWFx4BFxYnNDYzMhYVFAYjIiYlNDYzMhYVFAYjIiYCAGpdXosoKCgoi15dampdXosoKCgoi15dalZMTHEgISEgcUxMVlZMTHEgISEgcUxMVisrKlEmJiMFHBtWODc/Pzc4VhscBSMmJlEqK9UlGxslJRsbJQGAJRsbJSUbGyVAKCiLXl1qal1eiygoKCiLXl1qal1eiygoA6AhIHFMTFZWTExxICEhIHFMTFZWTExxICH+CQYGFRAQFEM6OlYYGRkYVjo6QxQQEBUGBvcoODgoKDg4KCg4OCgoODgAAAMAAP/ABAADwAAbADcAQwAAASIHDgEHBhUUFx4BFxYzMjc+ATc2NTQnLgEnJgMiJy4BJyY1NDc+ATc2MzIXHgEXFhUUBw4BBwYTBycHFwcXNxc3JzcCAGpdXosoKCgoi15dampdXosoKCgoi15dalZMTHEgISEgcUxMVlZMTHEgISEgcUxMSqCgYKCgYKCgYKCgA8AoKIteXWpqXV6LKCgoKIteXWpqXV6LKCj8YCEgcUxMVlZMTHEgISEgcUxMVlZMTHEgIQKgoKBgoKBgoKBgoKAAAQBl/8ADmwPAACkAAAEiJiMiBw4BBwYVFBYzLgE1NDY3MAcGAgcGBxUhEzM3IzceATMyNjcOAQMgRGhGcVNUbRobSUgGDWVKEBBLPDxZAT1sxizXNC1VJi5QGB09A7AQHh1hPj9BTTsLJjeZbwN9fv7Fj5AjGQIAgPYJDzdrCQcAAAAAAgAAAAAEAAOAAAkAFwAAJTMHJzMRIzcXIyURJyMRMxUhNTMRIwcRA4CAoKCAgKCggP8AQMCA/oCAwEDAwMACAMDAwP8AgP1AQEACwIABAAADAMAAAANAA4AAFgAfACgAAAE+ATU0Jy4BJyYjIREhMjc+ATc2NTQmATMyFhUUBisBEyMRMzIWFRQGAsQcIBQURi4vNf7AAYA1Ly5GFBRE/oRlKjw8KWafn58sPj4B2yJULzUvLkYUFPyAFBRGLi81RnQBRks1NUv+gAEASzU1SwAAAAACAMAAAANAA4AAHwAjAAABMxEUBw4BBwYjIicuAScmNREzERQWFx4BMzI2Nz4BNQEhFSECwIAZGVc6O0JCOzpXGRmAGxgcSSgoSRwYG/4AAoD9gAOA/mA8NDVOFhcXFk41NDwBoP5gHjgXGBsbGBc4Hv6ggAAAAAABAIAAAAOAA4AACwAAARUjATMVITUzASM1A4CA/sCA/kCAAUCAA4BA/QBAQAMAQAABAAAAAAQAA4AAPQAAARUjHgEVFAYHDgEjIiYnLgE1MxQWMzI2NTQmIyE1IS4BJy4BNTQ2Nz4BMzIWFx4BFSM0JiMiBhUUFjMyFhcEAOsVFjUwLHE+PnEsMDWAck5OcnJO/gABLAIEATA1NTAscT4+cSwwNYByTk5yck47bisBwEAdQSI1YiQhJCQhJGI1NExMNDRMQAEDASRiNTViJCEkJCEkYjU0TEw0NEwhHwAAAAcAAP/ABAADwAADAAcACwAPABMAGwAjAAATMxUjNzMVIyUzFSM3MxUjJTMVIwMTIRMzEyETAQMhAyMDIQMAgIDAwMABAICAwMDAAQCAgBAQ/QAQIBACgBD9QBADABAgEP2AEAHAQEBAQEBAQEBAAkD+QAHA/oABgPwAAYD+gAFA/sAAAAoAAAAABAADgAADAAcACwAPABMAFwAbAB8AIwAnAAATESERATUhFR0BITUBFSE1IxUhNREhFSElIRUhETUhFQEhFSEhNSEVAAQA/YABAP8AAQD/AED/AAEA/wACgAEA/wABAPyAAQD/AAKAAQADgPyAA4D9wMDAQMDAAgDAwMDA/wDAwMABAMDA/sDAwMAAAAUAAAAABAADgAADAAcACwAPABMAABMhFSEVIRUhESEVIREhFSERIRUhAAQA/AACgP2AAoD9gAQA/AAEAPwAA4CAQID/AIABQID/AIAAAAAABQAAAAAEAAOAAAMABwALAA8AEwAAEyEVIRchFSERIRUhAyEVIREhFSEABAD8AMACgP2AAoD9gMAEAPwABAD8AAOAgECA/wCAAUCA/wCAAAAFAAAAAAQAA4AAAwAHAAsADwATAAATIRUhBSEVIREhFSEBIRUhESEVIQAEAPwAAYACgP2AAoD9gP6ABAD8AAQA/AADgIBAgP8AgAFAgP8AgAAAAAABAD8APwLmAuYALAAAJRQPAQYjIi8BBwYjIi8BJjU0PwEnJjU0PwE2MzIfATc2MzIfARYVFA8BFxYVAuYQThAXFxCoqBAXFhBOEBCoqBAQThAWFxCoqBAXFxBOEBCoqBDDFhBOEBCoqBAQThAWFxCoqBAXFxBOEBCoqBAQThAXFxCoqBAXAAAABgAAAAADJQNuABQAKAA8AE0AVQCCAAABERQHBisBIicmNRE0NzY7ATIXFhUzERQHBisBIicmNRE0NzY7ATIXFhcRFAcGKwEiJyY1ETQ3NjsBMhcWExEhERQXFhcWMyEyNzY3NjUBIScmJyMGBwUVFAcGKwERFAcGIyEiJyY1ESMiJyY9ATQ3NjsBNzY3NjsBMhcWHwEzMhcWFQElBgUIJAgFBgYFCCQIBQaSBQUIJQgFBQUFCCUIBQWSBQUIJQgFBQUFCCUIBQVJ/gAEBAUEAgHbAgQEBAT+gAEAGwQGtQYEAfcGBQg3Ghsm/iUmGxs3CAUFBQUIsSgIFxYXtxcWFgkosAgFBgIS/rcIBQUFBQgBSQgFBgYFCP63CAUFBQUIAUkIBQYGBQj+twgFBQUFCAFJCAUGBgX+WwId/eMNCwoFBQUFCgsNAmZDBQICBVUkCAYF/eMwIiMhIi8CIAUGCCQIBQVgFQ8PDw8VYAUFCAACAAcASQO3Aq8AGgAuAAAJAQYjIi8BJjU0PwEnJjU0PwE2MzIXARYVFAcBFRQHBiMhIicmPQE0NzYzITIXFgFO/vYGBwgFHQYG4eEGBh0FCAcGAQoGBgJpBQUI/dsIBQUFBQgCJQgFBQGF/vYGBhwGCAcG4OEGBwcGHQUF/vUFCAcG/vslCAUFBQUIJQgFBQUFAAAAAQAjAAAD3QNuALMAACUiJyYjIgcGIyInJjU0NzY3Njc2NzY9ATQnJiMhIgcGHQEUFxYXFjMWFxYVFAcGIyInJiMiBwYjIicmNTQ3Njc2NzY3Nj0BETQ1NDU0JzQnJicmJyYnJicmIyInJjU0NzYzMhcWMzI3NjMyFxYVFAcGIwYHBgcGHQEUFxYzITI3Nj0BNCcmJyYnJjU0NzYzMhcWMzI3NjMyFxYVFAcGByIHBgcGFREUFxYXFhcyFxYVFAcGIwPBGTMyGhkyMxkNCAcJCg0MERAKEgEHFf5+FgcBFQkSEw4ODAsHBw4bNTUaGDExGA0HBwkJCwwQDwkSAQIBAgMEBAUIEhENDQoLBwcOGjU1GhgwMRgOBwcJCgwNEBAIFAEHDwGQDgcBFAoXFw8OBwcOGTMyGRkxMRkOBwcKCg0NEBEIFBQJEREODQoLBwcOAAICAgIMCw8RCQkBAQMDBQxE4AwFAwMFDNRRDQYBAgEICBIPDA0CAgICDAwOEQgJAQIDAwUNRSEB0AINDQgIDg4KCgsLBwcDBgEBCAgSDwwNAgICAg0MDxEICAECAQYMULYMBwEBBwy2UAwGAQEGBxYPDA0CAgICDQwPEQgIAQECBg1P/eZEDAYCAgEJCBEPDA0AAAIAAP+3A/8DtwATADkAAAEyFxYVFAcCBwYjIicmNTQ3ATYzARYXFh8BFgcGIyInJicmJyY1FhcWFxYXFjMyNzY3Njc2NzY3NjcDmygeHhq+TDdFSDQ0NQFtISn9+BcmJy8BAkxMe0c2NiEhEBEEExQQEBIRCRcIDxITFRUdHR4eKQO3GxooJDP+mUY0NTRJSTABSx/9sSsfHw0oek1MGhsuLzo6RAMPDgsLCgoWJRsaEREKCwQEAgABAAAAAAAA9evv618PPPUACwQAAAAAANbEBFgAAAAA1sQEWAAA/7cEAQPAAAAACAACAAAAAAAAAAEAAAPA/8AAAAQAAAD//wQBAAEAAAAAAAAAAAAAAAAAAAAhBAAAAAAAAAAAAAAAAgAAAAQAAAAEAAAABAAAAAQAAMAEAAAABAAAAAQAAAAEAABABAAAAAQAAAAEAAAeBAAAAAQAAAAEAABlBAAAAAQAAMAEAADABAAAgAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAMlAD8DJQAAA74ABwQAACMD/wAAAAAAAAAKABQAHgBMAJQA+AE2AXwBwgI2AnQCvgLoA34EHgSIBMoE8gU0BXAFiAXgBiIGagaSBroG5AcoB+AIKgkcCXgAAQAAACEAtAAKAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAA4ArgABAAAAAAABAAcAAAABAAAAAAACAAcAYAABAAAAAAADAAcANgABAAAAAAAEAAcAdQABAAAAAAAFAAsAFQABAAAAAAAGAAcASwABAAAAAAAKABoAigADAAEECQABAA4ABwADAAEECQACAA4AZwADAAEECQADAA4APQADAAEECQAEAA4AfAADAAEECQAFABYAIAADAAEECQAGAA4AUgADAAEECQAKADQApGljb21vb24AaQBjAG8AbQBvAG8AblZlcnNpb24gMS4wAFYAZQByAHMAaQBvAG4AIAAxAC4AMGljb21vb24AaQBjAG8AbQBvAG8Abmljb21vb24AaQBjAG8AbQBvAG8AblJlZ3VsYXIAUgBlAGcAdQBsAGEAcmljb21vb24AaQBjAG8AbQBvAG8AbkZvbnQgZ2VuZXJhdGVkIGJ5IEljb01vb24uAEYAbwBuAHQAIABnAGUAbgBlAHIAYQB0AGUAZAAgAGIAeQAgAEkAYwBvAE0AbwBvAG4ALgAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=) format(\'truetype\');  font-weight: normal;  font-style: normal;}[class^="w-e-icon-"],[class*=" w-e-icon-"] {  /* use !important to prevent issues with browser extensions that change fonts */  font-family: \'w-e-icon\' !important;  speak: none;  font-style: normal;  font-weight: normal;  font-variant: normal;  text-transform: none;  line-height: 1;  /* Better Font Rendering =========== */  -webkit-font-smoothing: antialiased;  -moz-osx-font-smoothing: grayscale;}.w-e-icon-close:before {  content: "\\f00d";}.w-e-icon-upload2:before {  content: "\\e9c6";}.w-e-icon-trash-o:before {  content: "\\f014";}.w-e-icon-header:before {  content: "\\f1dc";}.w-e-icon-pencil2:before {  content: "\\e906";}.w-e-icon-paint-brush:before {  content: "\\f1fc";}.w-e-icon-image:before {  content: "\\e90d";}.w-e-icon-play:before {  content: "\\e912";}.w-e-icon-location:before {  content: "\\e947";}.w-e-icon-undo:before {  content: "\\e965";}.w-e-icon-redo:before {  content: "\\e966";}.w-e-icon-quotes-left:before {  content: "\\e977";}.w-e-icon-list-numbered:before {  content: "\\e9b9";}.w-e-icon-list2:before {  content: "\\e9bb";}.w-e-icon-link:before {  content: "\\e9cb";}.w-e-icon-happy:before {  content: "\\e9df";}.w-e-icon-bold:before {  content: "\\ea62";}.w-e-icon-underline:before {  content: "\\ea63";}.w-e-icon-italic:before {  content: "\\ea64";}.w-e-icon-strikethrough:before {  content: "\\ea65";}.w-e-icon-table2:before {  content: "\\ea71";}.w-e-icon-paragraph-left:before {  content: "\\ea77";}.w-e-icon-paragraph-center:before {  content: "\\ea78";}.w-e-icon-paragraph-right:before {  content: "\\ea79";}.w-e-icon-terminal:before {  content: "\\f120";}.w-e-icon-page-break:before {  content: "\\ea68";}.w-e-icon-cancel-circle:before {  content: "\\ea0d";}.w-e-icon-font:before {  content: "\\ea5c";}.w-e-icon-text-heigh:before {  content: "\\ea5f";}.w-e-toolbar {  display: -webkit-box;  display: -ms-flexbox;  display: flex;  padding: 0 5px;  /* flex-wrap: wrap; */  /* 单个菜单 */}.w-e-toolbar .w-e-menu {  position: relative;  text-align: center;  padding: 5px 10px;  cursor: pointer;}.w-e-toolbar .w-e-menu i {  color: #999;}.w-e-toolbar .w-e-menu:hover i {  color: #333;}.w-e-toolbar .w-e-active i {  color: #1e88e5;}.w-e-toolbar .w-e-active:hover i {  color: #1e88e5;}.w-e-text-container .w-e-panel-container {  position: absolute;  top: 0;  left: 50%;  border: 1px solid #ccc;  border-top: 0;  box-shadow: 1px 1px 2px #ccc;  color: #333;  background-color: #fff;  /* 为 emotion panel 定制的样式 */  /* 上传图片的 panel 定制样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-close {  position: absolute;  right: 0;  top: 0;  padding: 5px;  margin: 2px 5px 0 0;  cursor: pointer;  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-close:hover {  color: #333;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title {  list-style: none;  display: -webkit-box;  display: -ms-flexbox;  display: flex;  font-size: 14px;  margin: 2px 10px 0 10px;  border-bottom: 1px solid #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-item {  padding: 3px 5px;  color: #999;  cursor: pointer;  margin: 0 3px;  position: relative;  top: 1px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-active {  color: #333;  border-bottom: 1px solid #333;  cursor: default;  font-weight: 700;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content {  padding: 10px 15px 10px 15px;  font-size: 16px;  /* 输入框的样式 */  /* 按钮的样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content button:focus {  outline: none;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea {  width: 100%;  border: 1px solid #ccc;  padding: 5px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus {  border-color: #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text] {  border: none;  border-bottom: 1px solid #ccc;  font-size: 14px;  height: 20px;  color: #333;  text-align: left;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].small {  width: 30px;  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].block {  display: block;  width: 100%;  margin: 10px 0;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text]:focus {  border-bottom: 2px solid #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button {  font-size: 14px;  color: #1e88e5;  border: none;  padding: 5px 10px;  background-color: #fff;  cursor: pointer;  border-radius: 3px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.left {  float: left;  margin-right: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.right {  float: right;  margin-left: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.gray {  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.red {  color: #c24f4a;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button:hover {  background-color: #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container:after {  content: "";  display: table;  clear: both;}.w-e-text-container .w-e-panel-container .w-e-emoticon-container .w-e-item {  cursor: pointer;  font-size: 18px;  padding: 0 3px;  display: inline-block;  *display: inline;  *zoom: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container {  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn {  display: inline-block;  *display: inline;  *zoom: 1;  color: #999;  cursor: pointer;  font-size: 60px;  line-height: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn:hover {  color: #333;}.w-e-text-container {  position: relative;}.w-e-text-container .w-e-progress {  position: absolute;  background-color: #1e88e5;  bottom: 0;  left: 0;  height: 1px;}.w-e-text {  padding: 0 10px;  overflow-y: scroll;}.w-e-text p,.w-e-text h1,.w-e-text h2,.w-e-text h3,.w-e-text h4,.w-e-text h5,.w-e-text table,.w-e-text pre {  margin: 10px 0;  line-height: 1.5;}.w-e-text ul,.w-e-text ol {  margin: 10px 0 10px 20px;}.w-e-text blockquote {  display: block;  border-left: 8px solid #d0e5f2;  padding: 5px 10px;  margin: 10px 0;  line-height: 1.4;  font-size: 100%;  background-color: #f1f1f1;}.w-e-text code {  display: inline-block;  *display: inline;  *zoom: 1;  background-color: #f1f1f1;  border-radius: 3px;  padding: 3px 5px;  margin: 0 3px;}.w-e-text pre code {  display: block;}.w-e-text table {  border-top: 1px solid #ccc;  border-left: 1px solid #ccc;}.w-e-text table td,.w-e-text table th {  border-bottom: 1px solid #ccc;  border-right: 1px solid #ccc;  padding: 3px 5px;}.w-e-text table th {  border-bottom: 2px solid #ccc;  text-align: center;}.w-e-text:focus {  outline: none;}.w-e-text img {  cursor: pointer;}.w-e-text img:hover {  box-shadow: 0 0 5px #333;}';

//  var inlinecss = '.w-e-toolbar,.w-e-text-container,.w-e-menu-panel {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-toolbar *,.w-e-menu-panel * {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-clear-fix:after {  content: "";  display: table;  clear: both;}.w-e-toolbar .w-e-droplist {  position: absolute;  left: 0;  top: 0;  background-color: #fff;  border: 1px solid #f1f1f1;  border-right-color: #ccc;  border-bottom-color: #ccc;}.w-e-toolbar .w-e-droplist .w-e-dp-title {  text-align: center;  color: #999;  line-height: 2;  border-bottom: 1px solid #f1f1f1;  font-size: 13px;}.w-e-toolbar .w-e-droplist ul.w-e-list {  list-style: none;  line-height: 1;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item {  color: #333;  padding: 5px 0;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item:hover {  background-color: #f1f1f1;}.w-e-toolbar .w-e-droplist ul.w-e-block {  list-style: none;  text-align: left;  padding: 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item {  display: inline-block;  *display: inline;  *zoom: 1;  padding: 3px 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item:hover {  background-color: #f1f1f1;}@font-face {  font-family: \'w-e-icon\';  src: url(data:application/font-woff;base64,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) format(\'truetype\');  font-weight: normal;  font-style: normal;}[class^="w-e-icon-"],[class*=" w-e-icon-"] {  /* use !important to prevent issues with browser extensions that change fonts */  font-family: \'w-e-icon\' !important;  speak: none;  font-style: normal;  font-weight: normal;  font-variant: normal;  text-transform: none;  line-height: 1;  /* Better Font Rendering =========== */  -webkit-font-smoothing: antialiased;  -moz-osx-font-smoothing: grayscale;}.w-e-icon-close:before {  content: "\\f00d";}.w-e-icon-upload2:before {  content: "\\e9c6";}.w-e-icon-trash-o:before {  content: "\\f014";}.w-e-icon-header:before {  content: "\\f1dc";}.w-e-icon-pencil2:before {  content: "\\e906";}.w-e-icon-paint-brush:before {  content: "\\f1fc";}.w-e-icon-image:before {  content: "\\e90d";}.w-e-icon-hide:before {  content: "\\e98f";}.w-e-icon-play:before {  content: "\\e912";}.w-e-icon-location:before {  content: "\\e947";}.w-e-icon-undo:before {  content: "\\e965";}.w-e-icon-redo:before {  content: "\\e966";}.w-e-icon-quotes-left:before {  content: "\\e977";}.w-e-icon-list-numbered:before {  content: "\\e9b9";}.w-e-icon-list2:before {  content: "\\e9bb";}.w-e-icon-link:before {  content: "\\e9cb";}.w-e-icon-file:before {  content: "\\e900";}.w-e-icon-happy:before {  content: "\\e9df";}.w-e-icon-bold:before {  content: "\\ea62";}.w-e-icon-underline:before {  content: "\\ea63";}.w-e-icon-italic:before {  content: "\\ea64";}.w-e-icon-strikethrough:before {  content: "\\ea65";}.w-e-icon-table2:before {  content: "\\ea71";}.w-e-icon-paragraph-left:before {  content: "\\ea77";}.w-e-icon-paragraph-center:before {  content: "\\ea78";}.w-e-icon-paragraph-right:before {  content: "\\ea79";}.w-e-icon-terminal:before {  content: "\\f120";}.w-e-icon-page-break:before {  content: "\\ea68";}.w-e-icon-cancel-circle:before {  content: "\\ea0d";}.w-e-icon-font:before {  content: "\\ea5c";}.w-e-icon-text-heigh:before {  content: "\\ea5f";}.w-e-toolbar {  display: -webkit-box;  display: -ms-flexbox;  display: flex;  padding: 0 5px;  /* flex-wrap: wrap; */  /* 单个菜单 */}.w-e-toolbar .w-e-menu {  position: relative;  text-align: center;  padding: 5px 10px;  cursor: pointer;}.w-e-toolbar .w-e-menu i {  color: #999;}.w-e-toolbar .w-e-menu:hover i {  color: #333;}.w-e-toolbar .w-e-active i {  color: #1e88e5;}.w-e-toolbar .w-e-active:hover i {  color: #1e88e5;}.w-e-text-container .w-e-panel-container {  position: absolute;  top: 0;  left: 50%;  border: 1px solid #ccc;  border-top: 0;  box-shadow: 1px 1px 2px #ccc;  color: #333;  background-color: #fff;  /* 为 emotion panel 定制的样式 */  /* 上传图片的 panel 定制样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-close {  position: absolute;  right: 0;  top: 0;  padding: 5px;  margin: 2px 5px 0 0;  cursor: pointer;  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-close:hover {  color: #333;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title {  list-style: none;  display: -webkit-box;  display: -ms-flexbox;  display: flex;  font-size: 14px;  margin: 2px 10px 0 10px;  border-bottom: 1px solid #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-item {  padding: 3px 5px;  color: #999;  cursor: pointer;  margin: 0 3px;  position: relative;  top: 1px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-active {  color: #333;  border-bottom: 1px solid #333;  cursor: default;  font-weight: 700;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content {  padding: 10px 15px 10px 15px;  font-size: 16px;  /* 输入框的样式 */  /* 按钮的样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content button:focus {  outline: none;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea {  width: 100%;  border: 1px solid #ccc;  padding: 5px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus {  border-color: #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text] {  border: none;  border-bottom: 1px solid #ccc;  font-size: 14px;  height: 20px;  color: #333;  text-align: left;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].small {  width: 30px;  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].block {  display: block;  width: 100%;  margin: 10px 0;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text]:focus {  border-bottom: 2px solid #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button {  font-size: 14px;  color: #1e88e5;  border: none;  padding: 5px 10px;  background-color: #fff;  cursor: pointer;  border-radius: 3px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.left {  float: left;  margin-right: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.right {  float: right;  margin-left: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.gray {  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.red {  color: #c24f4a;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button:hover {  background-color: #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container:after {  content: "";  display: table;  clear: both;}.w-e-text-container .w-e-panel-container .w-e-emoticon-container .w-e-item {  cursor: pointer;  font-size: 18px;  padding: 0 3px;  display: inline-block;  *display: inline;  *zoom: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container {  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn {  display: inline-block;  *display: inline;  *zoom: 1;  color: #999;  cursor: pointer;  font-size: 60px;  line-height: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn:hover {  color: #333;}.w-e-text-container {  position: relative;}.w-e-text-container .w-e-progress {  position: absolute;  background-color: #1e88e5;  bottom: 0;  left: 0;  height: 1px;}.w-e-text {  padding: 0 10px;  overflow-y: scroll;}.w-e-text p,.w-e-text h1,.w-e-text h2,.w-e-text h3,.w-e-text h4,.w-e-text h5,.w-e-text table,.w-e-text pre {  margin: 10px 0;  line-height: 1.5;}.w-e-text ul,.w-e-text ol {  margin: 10px 0 10px 20px;}.w-e-text blockquote {  display: block;  border-left: 8px solid #d0e5f2;  padding: 5px 10px;  margin: 10px 0;  line-height: 1.4;  font-size: 100%;  background-color: #f1f1f1;}.w-e-text code {  display: inline-block;  *display: inline;  *zoom: 1;  background-color: #f1f1f1;  border-radius: 3px;  padding: 3px 5px;  margin: 0 3px;}.w-e-text pre code {  display: block;}.w-e-text table {  border-top: 1px solid #ccc;  border-left: 1px solid #ccc;}.w-e-text table td,.w-e-text table th {  border-bottom: 1px solid #ccc;  border-right: 1px solid #ccc;  padding: 3px 5px;}.w-e-text table th {  border-bottom: 2px solid #ccc;  text-align: center;}.w-e-text:focus {  outline: none;}.w-e-text img {  cursor: pointer;}.w-e-text img:hover {  box-shadow: 0 0 5px #333;}';
  var inlinecss = '.w-e-toolbar,.w-e-text-container,.w-e-menu-panel {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-toolbar *,.w-e-menu-panel * {  padding: 0;  margin: 0;  box-sizing: border-box;}.w-e-clear-fix:after {  content: "";  display: table;  clear: both;}.w-e-toolbar .w-e-droplist {  position: absolute;  left: 0;  top: 0;  background-color: #fff;  border: 1px solid #f1f1f1;  border-right-color: #ccc;  border-bottom-color: #ccc;}.w-e-toolbar .w-e-droplist .w-e-dp-title {  text-align: center;  color: #999;  line-height: 2;  border-bottom: 1px solid #f1f1f1;  font-size: 13px;}.w-e-toolbar .w-e-droplist ul.w-e-list {  list-style: none;  line-height: 1;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item {  color: #333;  padding: 5px 0;}.w-e-toolbar .w-e-droplist ul.w-e-list li.w-e-item:hover {  background-color: #f1f1f1;}.w-e-toolbar .w-e-droplist ul.w-e-block {  list-style: none;  text-align: left;  padding: 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item {  display: inline-block;  *display: inline;  *zoom: 1;  padding: 3px 5px;}.w-e-toolbar .w-e-droplist ul.w-e-block li.w-e-item:hover {  background-color: #f1f1f1;}@font-face {  font-family: \'w-e-icon\';  src: url(data:application/font-woff;base64,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) format(\'truetype\');  font-weight: normal;  font-style: normal;}[class^="w-e-icon-"],[class*=" w-e-icon-"] {  /* use !important to prevent issues with browser extensions that change fonts */  font-family: \'w-e-icon\' !important;  speak: none;  font-style: normal;  font-weight: normal;  font-variant: normal;  text-transform: none;  line-height: 1;  /* Better Font Rendering =========== */  -webkit-font-smoothing: antialiased;  -moz-osx-font-smoothing: grayscale;}.w-e-icon-close:before {  content: "\\f00d";}.w-e-icon-upload2:before {  content: "\\e9c6";}.w-e-icon-trash-o:before {  content: "\\f014";}.w-e-icon-header:before {  content: "\\f1dc";}.w-e-icon-pencil2:before {  content: "\\e906";}.w-e-icon-paint-brush:before {  content: "\\f1fc";}.w-e-icon-image:before {  content: "\\e90d";}.w-e-icon-hide:before {  content: "\\e901";}.w-e-icon-play:before {  content: "\\e912";}.w-e-icon-location:before {  content: "\\e947";}.w-e-icon-undo:before {  content: "\\e965";}.w-e-icon-redo:before {  content: "\\e966";}.w-e-icon-quotes-left:before {  content: "\\e977";}.w-e-icon-list-numbered:before {  content: "\\e9b9";}.w-e-icon-list2:before {  content: "\\e9bb";}.w-e-icon-link:before {  content: "\\e9cb";}.w-e-icon-file:before {  content: "\\e902";}.w-e-icon-happy:before {  content: "\\e9df";}.w-e-icon-bold:before {  content: "\\ea62";}.w-e-icon-underline:before {  content: "\\ea63";}.w-e-icon-italic:before {  content: "\\ea64";}.w-e-icon-strikethrough:before {  content: "\\ea65";}.w-e-icon-table2:before {  content: "\\ea71";}.w-e-icon-paragraph-left:before {  content: "\\ea77";}.w-e-icon-paragraph-center:before {  content: "\\ea78";}.w-e-icon-paragraph-right:before {  content: "\\ea79";}.w-e-icon-terminal:before {  content: "\\e923";}.w-e-icon-page-break:before {  content: "\\ea68";}.w-e-icon-cancel-circle:before {  content: "\\ea0d";}.w-e-icon-font:before {  content: "\\ea5c";}.w-e-icon-text-heigh:before {  content: "\\ea5f";}.w-e-toolbar {  display: -webkit-box;  display: -ms-flexbox;  display: flex;  padding: 0 5px;  /* flex-wrap: wrap; */  /* 单个菜单 */}.w-e-toolbar .w-e-menu {  position: relative;  text-align: center;  padding: 5px 10px;  cursor: pointer;}.w-e-toolbar .w-e-menu i {  color: #999;}.w-e-toolbar .w-e-menu:hover i {  color: #333;}.w-e-toolbar .w-e-active i {  color: #1e88e5;}.w-e-toolbar .w-e-active:hover i {  color: #1e88e5;}.w-e-text-container .w-e-panel-container {  position: absolute;  top: 0;  left: 50%;  border: 1px solid #ccc;  border-top: 0;  box-shadow: 1px 1px 2px #ccc;  color: #333;  background-color: #fff;  /* 为 emotion panel 定制的样式 */  /* 上传图片的 panel 定制样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-close {  position: absolute;  right: 0;  top: 0;  padding: 5px;  margin: 2px 5px 0 0;  cursor: pointer;  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-close:hover {  color: #333;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title {  list-style: none;  display: -webkit-box;  display: -ms-flexbox;  display: flex;  font-size: 14px;  margin: 2px 10px 0 10px;  border-bottom: 1px solid #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-item {  padding: 3px 5px;  color: #999;  cursor: pointer;  margin: 0 3px;  position: relative;  top: 1px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-title .w-e-active {  color: #333;  border-bottom: 1px solid #333;  cursor: default;  font-weight: 700;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content {  padding: 10px 15px 10px 15px;  font-size: 16px;  /* 输入框的样式 */  /* 按钮的样式 */}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus,.w-e-text-container .w-e-panel-container .w-e-panel-tab-content button:focus {  outline: none;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea {  box-sizing:border-box;-o-box-sizing:border-box;-ms-box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;background: #ffffff;border: 1px solid #eee;outline: none; color: rgba(0,0,0,0.8);border-radius: 3px;line-height: 24px;font-size: 16px;width: 100%;padding: 6px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content textarea:focus {  border-color: #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text] {  border: none;  border-bottom: 1px solid #ccc;  font-size: 14px;  height: 20px;  color: #333;  text-align: left;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].small {  width: 30px;  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text].block {  display: block;  width: 100%;  margin: 10px 0;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content input[type=text]:focus {  border-bottom: 2px solid #1e88e5;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button {  font-size: 14px;  color: #1e88e5;  border: none;  padding: 5px 10px;  background-color: #fff;  cursor: pointer;  border-radius: 3px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container div.left {  float: left;  margin-right: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container div.left select{height:26px; line-height:26px; padding:2px 4px;border:1px solid;border-color:#e0e0e0 #e0e0e0 #e0e0e0 #e0e0e0;background:#fff;border-radius:2px;margin-right: 1px;margin-top: 3px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.left {  float: left;  margin-right: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.right {  float: right;  margin-left: 10px;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.gray {  color: #999;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button.red {  color: #c24f4a;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container button:hover {  background-color: #f1f1f1;}.w-e-text-container .w-e-panel-container .w-e-panel-tab-content .w-e-button-container:after {  content: "";  display: table;  clear: both;}.w-e-text-container .w-e-panel-container .w-e-emoticon-container .w-e-item {  cursor: pointer;  font-size: 18px;  padding: 0 3px;  display: inline-block;  *display: inline;  *zoom: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container {  text-align: center;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn {  display: inline-block;  *display: inline;  *zoom: 1;  color: #999;  cursor: pointer;  font-size: 60px;  line-height: 1;}.w-e-text-container .w-e-panel-container .w-e-up-img-container .w-e-up-btn:hover {  color: #333;}.w-e-text-container {  position: relative;}.w-e-text-container .w-e-progress {  position: absolute;  background-color: #1e88e5;  bottom: 0;  left: 0;  height: 1px;}.w-e-text {  padding: 0 10px;  overflow-y: scroll;}.w-e-text p,.w-e-text h1,.w-e-text h2,.w-e-text h3,.w-e-text h4,.w-e-text h5,.w-e-text table,.w-e-text pre {  margin: 10px 0;  line-height: 1.5;}.w-e-text ul,.w-e-text ol {  margin: 10px 0 10px 20px;}.w-e-text blockquote {  display: block;  border-left: 8px solid #d0e5f2;  padding: 5px 10px;  margin: 10px 0;  line-height: 1.4;  font-size: 100%;  background-color: #f1f1f1;}.w-e-text code {  display: inline-block;  *display: inline;  *zoom: 1;  background-color: #f1f1f1;  border-radius: 3px;  padding: 3px 5px;  margin: 0 3px;}.w-e-text pre code {  display: block;}.w-e-text table {  border-top: 1px solid #ccc;  border-left: 1px solid #ccc;}.w-e-text table td,.w-e-text table th {  border-bottom: 1px solid #ccc;  border-right: 1px solid #ccc;  padding: 3px 5px;}.w-e-text table th {  border-bottom: 2px solid #ccc;  text-align: center;}.w-e-text:focus {  outline: none;}.w-e-text img {  cursor: pointer;}.w-e-text img:hover {  box-shadow: 0 0 5px #333;}';

  inlinecss += '.w-e-panel-tab-content .radio-input {  display: none;}';
  inlinecss += '.w-e-panel-tab-content .radio-input:checked + .radio-core {  background-color: #26a2ff;  border-color: #26a2ff;}';
  inlinecss += '.w-e-panel-tab-content .radio-input:checked + .radio-core::after {  background-color: #fff;  -webkit-transform: scale(1);  transform: scale(1);}';
  inlinecss += '.w-e-panel-tab-content .radio-input[disabled] + .radio-core {  background-color: #d9d9d9;  border-color: #ccc;}';
  inlinecss += '.w-e-panel-tab-content .radio-core {  margin-top:-2px;  box-sizing: border-box;  display: inline-block;  background-color: #fff;  border-radius: 100%;  border: 1px solid #ccc;  position: relative;  width: 17px;  height: 17px;  vertical-align: middle;}';
  inlinecss += '.w-e-panel-tab-content .radio-core::after {  content: " ";  border-radius: 100%;  top: 4px;  left: 4px;  position: absolute;  width: 7px;  height: 7px;  -webkit-transition: -webkit-transform .2s;  transition: -webkit-transform .2s;  transition: transform .2s;  transition: transform .2s, -webkit-transform .2s;  -webkit-transform: scale(0);  transform: scale(0);}';
  inlinecss += '.w-e-panel-tab-content .radio-title {  font-size: 14px;  margin-left: 5px;  line-height: 28px;}';
  
  inlinecss += '.editor-text hide { position: relative; border: 0;  border-left: 3px solid #06b5ff;  margin-left: 10px;  padding: 0.5em;  display: block;  min-height:26px;  margin: 30px 0px 0px 0px;}';
  inlinecss += ".editor-text .inputValue_10::before {  content: '密码: ' attr(input-value) '';  color: #06b5ff;  font-size:14px;  position: absolute;  margin-top: -30px;  line-height: 30px;}";
  inlinecss += ".editor-text .inputValue_20::before {  content: '回复话题可见';  color: #06b5ff;  font-size:14px;  position: absolute;  margin-top: -30px;  line-height: 30px;}";
  inlinecss += ".editor-text .inputValue_30::before {  content: '达到等级 ' attr(description) ' 可见';  color: #06b5ff;  font-size:14px;  position: absolute;  margin-top: -30px;  line-height: 30px;}";
  inlinecss += ".editor-text .inputValue_40::before {  content: '需要支付 ' attr(input-value) ' 积分可见';  color: #06b5ff;  font-size:14px;  position: absolute;  margin-top: -30px;  line-height: 30px;}";
  inlinecss += ".editor-text .inputValue_50::before {  content: '需要支付 ' attr(input-value) ' 元费用可见';  color: #06b5ff;  font-size:14px;  position: absolute;  margin-top: -30px;  line-height: 30px;}";
 // inlinecss += ".editor-text hide::after{content: '需见';  color: #06b5ff;  font-size:14px;  position: absolute;left: 10px; bottom:-20px;  line-height: 60px;}";
 // inlinecss += ".editor-text hide::after{content: ''; position: absolute;left: 10px; bottom:-20px; display: inline-block; width: 8px;height: 8px;border-top: 1px solid #06b5ff;border-right: 1px solid #06b5ff;transform: rotate(135deg);-webkit-transform: rotate(135deg);}";
 // inlinecss += ".editor-text hide::after{content: ''; position: absolute;left: 0px; bottom:-2px; display: inline-block; width: 8px;height: 8px;border-top: 1px solid #06b5ff;border-right: 1px solid #06b5ff;transform: rotate(180deg);-webkit-transform: rotate(180deg);-moz-animation: bounce 2s infinite; -webkit-animation: bounce 2s infinite;animation: bounce 2s infinite;}";
  inlinecss += ".editor-text hide::after{font-family: 'w-e-icon' !important;content: '\\e900';font-size:12px;color:#06b5ff;  position: absolute;left: -11px; bottom:-2px; -moz-animation: bounce 2s infinite; -webkit-animation: bounce 2s infinite;animation: bounce 2s infinite;}";
  
  //弹跳动画
  inlinecss += "@keyframes bounce {0%, 20%, 50%, 80%, 100% { transform: translateY(0);}40% {transform: translateY(-8px);}60% {transform: translateY(-4px);}}";
  
  
  

  
  inlinecss += '.editor-text .playerImg {  border: 1px solid #ddd;  padding:50px; width: 64px; height: 64px;}';

  inlinecss += '.w-e-text pre code{min-height: 20px;}';
  
  
  
  // 将 css 代码添加到 <style> 中
var style = document.createElement('style');
style.type = 'text/css';
style.innerHTML = inlinecss;
document.getElementsByTagName('HEAD').item(0).appendChild(style);

// 返回
var index = window.wangEditor || Editor;

return index;

})));
