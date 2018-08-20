/**
 * 每周期
 */
function everyTime(dom) {
	var item = $("input[name=v_" + dom.name + "]");
	item.val("*");
	item.change();
}

/**
 * 不指定
 */
function unAppoint(dom) {
	var name = dom.name;
	var val = "?";
	if (name == "year")
		val = "";
	var item = $("input[name=v_" + name + "]");
	item.val(val);
	item.change();
}

function appoint(dom) {

}

/**
 * 周期
 */
function cycle(dom) {
	var name = dom.name;
	var ns = $(dom).parent().find(".numberspinner");

	var start = ns.eq(0).val();
	var end = ns.eq(1).val();
	var item = $("input[name=v_" + name + "]");
	item.val(start + "-" + end);
	item.change();
}

/**
 * 从开始
 */
function startOn(dom) {
	var name = dom.name;
	var ns = $(dom).parent().find(".numberspinner");
	var start = ns.eq(0).val();
	var end = ns.eq(1).val();
	var item = $("input[name=v_" + name + "]");
	item.val(start + "/" + end);
	item.change();
}

/**
 * 最后一天
 * @param dom
 */
function lastDay(dom){
	var item = $("input[name=v_" + dom.name + "]");
	item.val("L");
	item.change();
}

function weekOfDay(dom){
	var name = dom.name;
	var ns = $(dom).parent().find(".numberspinner");
	var start = ns.eq(0).val();
	var end = ns.eq(1).val();
	var item = $("input[name=v_" + name + "]");
	item.val(start + "#" + end);
	item.change();
}

function lastWeek(dom){
	var item = $("input[name=v_" + dom.name + "]");
	var ns = $(dom).parent().find(".numberspinner");
	var start = ns.eq(0).val();
	item.val(start+"L");
	item.change();
}

function workDay(dom) {
	var name = dom.name;
	var ns = $(dom).parent().find(".numberspinner");
	var start = ns.eq(0).val();
	var item = $("input[name=v_" + name + "]");
	item.val(start + "W");
	item.change();
}




$(function() {
	/**
	$(".numberspinner").numberspinner({
		onChange:function(){
			$(this).closest("span.line").children().eq(0).click();
		}
	});**/
	
	
	$(".numberspinner").each(function() {
	//	alert(this.id);
		
	//	$("#"+this.id).after("<span class=\"spinner\" style=\"width: 58px; height: 20px;\"><input style=\"width: 36px; height: 20px; line-height: 20px;\" id=\"secondStart_0\" class=\"numberspinner numberspinner-f spinner-text spinner-f validatebox-text numberbox-f\" value=\"1" data-options="min:1,max:58" min="1" max="58"><input type="hidden" value="1"><span class="spinner-arrow" style="height: 20px;"><span class="spinner-arrow-up" style="height: 10px;"></span><span class="spinner-arrow-down" style="height: 10px;"></span></span></span>");
	//	var input = "<input type='text'>";
	//	this.append(input);
		var min = parseInt(this.min);
		var max = parseInt(this.max);
		 var options = {
				min: min,
				max: max,
	            stop:function(event,ui){
	            	$(this).closest("span.line").children().eq(0).click();
	            }
	       //     icons: { down: "custom-down-icon", up: "custom-up-icon" }

			};
		 $("#"+this.id).spinner(options);
		 $("#"+this.id).click(function () {
			if(parseInt(this.value) < min){
				this.value = min;
			}
			if(parseInt(this.value) >max){
				this.value = max;
			}
			 $(this).closest("span.line").children().eq(0).click();
		 });
		 
		 $("#"+this.id).change(function(){
			if(parseInt(this.value) < min){
			//	$("#"+this.id).val(min);
				this.value = min;
			}
			 
			if(parseInt(this.value) >max){
			//	$("#"+this.id).val(max);
				this.value = max;
			}
			 $(this).closest("span.line").children().eq(0).click();
			 
			 
		 });
	});


	var vals = $("input[name^='v_']");
	var cron = $("#cron");
	vals.change(function() {
		var item = [];
		vals.each(function() {
			item.push(this.value);
		});
		cron.val(item.join(" "));
	});
	
	var secondList = $(".secondList").children();
	$("#sencond_appoint").click(function(){
		if(this.checked){
			secondList.eq(0).change();
		}
	});

	secondList.change(function() {
	//	var sencond_appoint = $("#sencond_appoint").prop("checked");
	//	if (sencond_appoint) {
			
		$("#sencond_appoint").attr("checked", "checked");
			
		var vals = [];
		secondList.each(function() {
			if (this.checked) {
				vals.push(this.value);
			}
		});
		var val = "?";
		if (vals.length > 0 && vals.length < 59) {
			val = vals.join(","); 
		}else if(vals.length == 59){
			val = "*";
		}
		var item = $("input[name=v_second]");
		item.val(val);
		item.change();
	//	}
	});
	
	var minList = $(".minList").children();
	$("#min_appoint").click(function(){
		if(this.checked){
			minList.eq(0).change();
		}
	});
	
	minList.change(function() {
	//	var min_appoint = $("#min_appoint").prop("checked");
		$("#min_appoint").attr("checked", "checked");
	//	if (min_appoint) {
			var vals = [];
			minList.each(function() {
				if (this.checked) {
					vals.push(this.value);
				}
			});
			var val = "?";
			if (vals.length > 0 && vals.length < 59) {
				val = vals.join(",");
			}else if(vals.length == 59){
				val = "*";
			}
			var item = $("input[name=v_min]");
			item.val(val);
			item.change();
	//	}
	});
	
	var hourList = $(".hourList").children();
	$("#hour_appoint").click(function(){
		if(this.checked){
			hourList.eq(0).change();
		}
	});
	
	hourList.change(function() {
	//	var hour_appoint = $("#hour_appoint").prop("checked");
	//	if (hour_appoint) {
		$("#hour_appoint").attr("checked", "checked");
			var vals = [];
			hourList.each(function() {
				if (this.checked) {
					vals.push(this.value);
				}
			});
			var val = "?";
			if (vals.length > 0 && vals.length < 24) {
				val = vals.join(",");
			}else if(vals.length == 24){
				val = "*";
			}
			var item = $("input[name=v_hour]");
			item.val(val);
			item.change();
	//	}
	});
	
	var dayList = $(".dayList").children();
	$("#day_appoint").click(function(){
		if(this.checked){
			dayList.eq(0).change();
		}
	});
	
	dayList.change(function() {
	//	var day_appoint = $("#day_appoint").prop("checked");
	//	if (day_appoint) {
		$("#day_appoint").attr("checked", "checked");
			var vals = [];
			dayList.each(function() {
				if (this.checked) {
					vals.push(this.value);
				}
			});
			var val = "?";
			if (vals.length > 0 && vals.length < 31) {
				val = vals.join(",");
			}else if(vals.length == 31){
				val = "*";
			}
			var item = $("input[name=v_day]");
			item.val(val);
			item.change();
	//	}
	});
	
	var mouthList = $(".mouthList").children();
	$("#mouth_appoint").click(function(){
		if(this.checked){
			mouthList.eq(0).change();
		}
	});
	
	mouthList.change(function() {
	//	var mouth_appoint = $("#mouth_appoint").prop("checked");
	//	if (mouth_appoint) {
		$("#mouth_appoint").attr("checked", "checked");
			var vals = [];
			mouthList.each(function() {
				if (this.checked) {
					vals.push(this.value);
				}
			});
			var val = "?";
			if (vals.length > 0 && vals.length < 12) {
				val = vals.join(",");
			}else if(vals.length == 12){
				val = "*";
			}
			var item = $("input[name=v_mouth]");
			item.val(val);
			item.change();
	//	}
	});
	
	var weekList = $(".weekList").children();
	$("#week_appoint").click(function(){
		if(this.checked){
			weekList.eq(0).change();
		}
	});
	
	weekList.change(function() {
	//	var week_appoint = $("#week_appoint").prop("checked");
	//	if (week_appoint) {
		$("#week_appoint").attr("checked", "checked");
			var vals = [];
			weekList.each(function() {
				if (this.checked) {
					vals.push(this.value);
				}
			});
			var val = "?";
			if (vals.length > 0 && vals.length < 7) {
				val = vals.join(",");
			}else if(vals.length == 7){
				val = "*";
			}
			var item = $("input[name=v_week]");
			item.val(val);
			item.change();
	//	}
	});
});