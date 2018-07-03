/** 
 * 文字向上滚动组件
 * 参数:
 * 	id:要滚动的ul标签Id
 *  li_height指li标签的高度 
 *  speed指速度
 *  delay是指延时
 *  index是ID的第几个 
 *  
 *  参考结构
 *  <div style="height:30px; overflow:hidden;">  
 *	    <ul id="scrollId">  
 *		    <li>1向上</li> 
 *		    <li>2向上</li>
 *	    </ul>  
 *  </div>  
 *  
 */ 
var scrollTool = {}; 
scrollTool.up = function(id,li_height,speed,delay){ 
    var t,s = false,objH; // s=false 是指默认情况下是滚动的 当s=true是不滚动的 
   
    var eID = document.getElementById(id); 
    if(eID != null){
    	eID.innerHTML +=eID.innerHTML; 
        eID.style.marginTop = 0; 
        eID.onmouseover = function(){ 
            s = true; 
        } 
        eID.onmouseout = function(){ 
            s = false; 
        } 
    }
    
    function eStart(){ 
    	if(eID != null){
    		 objH = eID.offsetHeight; 
    	     eID.style.height = objH; 
    	     t = setInterval(eScroll,speed); 
    	     if(!s){eID.style.marginTop = parseInt(eID.style.marginTop)-1 + "px"} //目的是为了文字向上滚动时 让容器变为负数 
    	}
       
    } 
    function eScroll(){ 
        if(parseInt(eID.style.marginTop)%li_height!=0){ 
            eID.style.marginTop = parseInt(eID.style.marginTop)-1 + "px"; 
            if(Math.abs(parseInt(eID.style.marginTop))>=objH/2) eID.style.marginTop = 0; 
        }else{ 
            clearInterval(t); 
            setTimeout(eStart,delay); 
        } 
    } 
    setTimeout(eStart,delay); 
} 