/**
 * Created by 陈熠 on 2017/6/21
 * email   :  228112142@qq.com
 * q群      ：     275846351
 * 穿梭框
 */
(function ($) {
    /* 入口函数 */
    $.fn.transferTool = function () {
        //当前表格对象
        var $grid = this;
        //获取表格参数
        cyProps = $grid.attr("cyProps");
        if (!cyProps) {
            return
        }
        cyProps = cyProps ? cyProps : "";
        //将表格参数转为json
        cyProps = eval("({" + cyProps + "})");
        //获取数据的地址，只能通过表码或url，如果两个都写，默认是url
        //从表码获取数据
        var codeName = cyProps.codeName;
        //从后台获取数据
        var url = cyProps.url;
        //从枚举获取数据
        var enumName = cyProps.enumName;
        var R = "";
        //如果是通过表码取值
        if (codeName != undefined && codeName != "") {
            R = checkboxTool.getDataByCode(cyProps.codeName);
        }
        //如果是从后台获取数据
        if (url != undefined && url != "") {
            R = checkboxTool.getDataByUrl(cyProps.url);
        }
        //如果是从枚举获取数据
        if (enumName != undefined && url != "") {
            R = checkboxTool.getDataByEnum(cyProps.enumName);
        }
        checkboxTool.renderData(R, $grid, cyProps);


    };
    /*默认配置*/
    var cyProps = {};

    /*方法对象*/
    var checkboxTool = {
        /**通过url获取数据 by chenyi 2017/7/5*/
        getDataByUrl: function (url) {
            var data;
            $.ajax({
                url: url,
                async: false,
                type: 'post',
                dataType: "json",
                success: function (R) {
                    if (R.code == 0) {
                        data = R;
                    } else {
                        data = {};
                        alert(R.msg);
                    }
                }
            });
            return data;
        },
        /**获取数据 by chenyi 2017/7/5*/
        getDataByCode: function (codeName) {
            /**localStorage是否已存在该数据*/
            var data = $t.getStorageItem(codeName);
            if (!data) {
                $.ajax({
                    url: '/getData/getCodeValues',//字典获取接口
                    async: false,
                    data: {codeName: codeName},
                    type: 'post',
                    dataType: "json",
                    success: function (R) {
                        if (R.code == 0) {
                            data = R;
                            /**设置localStorage缓存*/
                            $t.setStorageItem(codeName, data);
                        } else {
                            data = {};
                            alert(R.msg);
                        }
                    }
                });

            }

            return data;
        },
        /**获取数据 by chenyi 2017/7/19*/
        getDataByEnum: function (enumName) {
            /**localStorage是否已存在该数据*/
            var data = $t.getStorageItem(enumName);
            if (!data) {
                $.ajax({
                    url: '/getData/getEnum',//使用枚举渲染  可联系作者q 228112142
                    async: false,
                    type: 'post',
                    data: {enumName: enumName},
                    dataType: "json",
                    success: function (R) {
                        if (R.code == 0) {
                            data = R;
                            /**设置localStorage缓存*/
                            $t.setStorageItem(enumName, data);
                        } else {
                            data = {};
                            alert(R.msg);
                        }
                    }
                });
            }
            return data;
        },
        /**渲染数据 by chenyi 2017/6/21*/
        renderData: function (R, $grid, cyProps) {
            var _grid = $grid;
            //获取下拉控件的name
            var _name = $(_grid).attr("name");
            if (_name.indexOf("[") == -1) {
                _name += "[]";
            }
            //获取下拉控件的默认值
            var _value = $(_grid).attr("value");
            var _values = _value.split(",");
            $(_grid).removeAttr("name");
            $(_grid).removeAttr("value");
            //获取监控标识
            var filter = cyProps.filter || "";
            //获取复选框禁用的值
            var _disabled = cyProps.disabled || "";
            var _disableds = _disabled.split(",");

            //是否开启级联(如果是数据源必须指定为url)
            var _cascade = $(_grid).attr("cascade") || "false";
            //查询款/级联父级
            var _searchHtml = "";
            //如果开启级联
            if (_cascade === "true") {
                if(!cyProps.url){
                    layer.open("级联模式下,请将数据源配置为url");
                }else{
                    _searchHtml = [
                        '<dd lay-value="" class="transfer-search-div">',
                        '<div  cyType="selectTool" cyProps="url:\''+cyProps.url+'\'"></div>',
                        '</dd>'
                    ].join("");
                }

            }
            if (_cascade === "false") {
                _searchHtml = [
                    '<dd lay-value="" class="transfer-search-div">',
                    '<i class="layui-icon  drop-search-btn"></i>',
                    '<input class="layui-input search_condition" placeholder="关键字搜索">',
                    '<i class="layui-icon  clear-btn search-clear-btn">&#x1006;</i>',
                    '</dd>'
                ].join("");
            }

            var data = R.data;
            //选中列表
            var leftList = "";
            //未选中列表
            var rightList = "";

            if (data !== undefined) {
                for (var i = 0; i < data.length; i++) {
                    //设置默认值(向右侧插入元素)
                    if (_values.indexOf(data[i].code) == -1) {
                        var _input = '<dd lay-value="' + data[i].code + '" lay-title="' + data[i].value + '"><input type="checkbox" lay-filter="transferLeftChecked" title="' + data[i].value + '" lay-skin="primary"></dd>';
                        //设置禁用
                        if (_disableds.indexOf(data[i].code) != -1) {
                            _input = _input.replace("<input", "<input disabled ")
                        }
                        leftList += _input;
                    }
                    //像左侧插入元素
                    else {
                        var _input = '<dd lay-value="' + data[i].code + '"  lay-title="' + data[i].value + '"><input type="hidden" name="' + _name + '" value="' + data[i].code + '"><input lay-filter="transferRightChecked"   type="checkbox"  title="' + data[i].value + '" lay-skin="primary"></dd>';
                        //设置禁用
                        if (_disableds.indexOf(data[i].code) != -1) {
                            _input = _input.replace("<input", "<input disabled ")
                        }
                        rightList += _input;
                    }
                    $(_grid).append(_input);
                }
            }
            /** 渲染结果**/
            var outHtml =
                _grid.html([
                    '<div class="transfer-content" style="width: 480px;height: 400px;position: relative">',
                    '<div class="transfer-panel transfer-panel-left">',
                    _searchHtml,
                    '<div class="transfer-div">',
                    leftList,
                    '</div>',
                    '</div>',
                    '<div class="transfer-btn transfer-to-right">',
                    '<button title="右移" lay-name="' + _name + '" class="layui-btn layui-btn-normal layui-btn-sm layui-btn-disabled"><i class="layui-icon">&#xe65b;</i></button>',
                    '</div>',
                    '<div class="transfer-btn  transfer-to-left">',
                    '<button title="左移" lay-name="' + _name + '"  class="layui-btn layui-btn-normal layui-btn-sm layui-btn-disabled"><i class="layui-icon">&#xe65a;</i></button>',
                    '</div>',
                    '<div class="transfer-panel transfer-panel-right">',
                    '<dd lay-value="" class="transfer-search-div">',
                    '<span  class="transfer-title" >',
                    ' 已选列表',
                    '</span>',
                    '</dd>',
                    '<div class="transfer-div">',
                    rightList,
                    '</div>',
                    '</div>',
                    '</div>'
                ].join(""));
            _grid.append(outHtml);


        }
    };


    //穿梭框选中监听
    layui.use('form', function () {
        var form = layui.form;
        //左侧选中
        form.on('checkbox(transferLeftChecked)', function (data) {
            var $this = $(data.othis);
            var _parent = $this.parents(".transfer-content");
            var inputs = $this.parents(".transfer-div").find("dd input[type='checkbox']");
            for (var i = 0; i < inputs.length; i++) {
                if ($(inputs[i]).is(':checked')) {
                    _parent.find(".transfer-to-right button").removeClass("layui-btn-disabled");
                    break;
                }
                _parent.find(".transfer-to-right button").addClass("layui-btn-disabled");
            }
        });
        //右侧选中
        form.on('checkbox(transferRightChecked)', function (data) {
            var $this = $(data.othis);
            var _parent = $this.parents(".transfer-content");
            var inputs = $this.parents(".transfer-div").find("dd input[type='checkbox']");
            for (var i = 0; i < inputs.length; i++) {
                if ($(inputs[i]).is(':checked')) {
                    _parent.find(".transfer-to-left button").removeClass("layui-btn-disabled");
                    break;
                }
                _parent.find(".transfer-to-left button").addClass("layui-btn-disabled");
            }
        });
    });


    //右移监听
    $(document).on("click", ".transfer-to-right", function () {
        var $this = $(this);
        var _name = $this.attr("lay-name") || "";
        var $parent = $this.parents(".transfer-content");
        var inputs = $parent.find(".transfer-panel-left .transfer-div").find("dd input[type='checkbox']");
        for (var i = 0; i < inputs.length; i++) {
            if ($(inputs[i]).is(':checked')) {
                //右侧添加
                var _value = $(inputs[i]).parents("dd").attr("lay-value");
                var _title = $(inputs[i]).parents("dd").attr("lay-title");
                var _input = ['<dd lay-value="' + _value + '" lay-title="' + _title + '"><input type="hidden" name="' + _name + '" value="' + _value + '">',
                    '<input lay-filter="transferRightChecked"  ',
                    ' type="checkbox"  title="' + _title + '" lay-skin="primary"></dd>'
                ].join("");
                _value && _title && $parent.find(".transfer-panel-right .transfer-div").append(_input);
                //左侧删除
                $(inputs[i]).parents("dd").remove();
            }
        }
        //重置按钮禁用
        $parent.find(".transfer-to-right button").addClass("layui-btn-disabled");

        layui.use('form', function () {
            var form = layui.form;
            form.render('checkbox');
        });
    });

    //左移监听
    $(document).on("click", ".transfer-to-left", function () {
        var $this = $(this);
        var $parent = $this.parents(".transfer-content");
        var inputs = $parent.find(".transfer-panel-right .transfer-div").find("dd input[type='checkbox']");
        for (var i = 0; i < inputs.length; i++) {
            if ($(inputs[i]).is(':checked')) {
                //右侧添加
                var _value = $(inputs[i]).parents("dd").attr("lay-value");
                var _title = $(inputs[i]).parents("dd").attr("lay-title");
                var _input = ['<dd lay-value="' + _value + '" lay-title="' + _title + '">',
                    '<input lay-filter="transferLeftChecked"  ',
                    ' type="checkbox"  title="' + _title + '" lay-skin="primary"></dd>'
                ].join("");
                _value && _title && $parent.find(".transfer-panel-left .transfer-div").append(_input);
                //右侧删除
                $(inputs[i]).parents("dd").remove();
            }
        }
        //重置按钮禁用
        $parent.find(".transfer-to-left button").addClass("layui-btn-disabled");

        layui.use('form', function () {
            var form = layui.form;
            form.render('checkbox');
        });
    });

    /**搜索监听  **/
    $(document).on("keypress", " .transfer-search-div .search_condition", function (e) {
        e.stopPropagation();
        //是否为Enter键
        if (/^13$/.test(e.keyCode)) {
            searchData($(this));

        }

    });
    /**清空搜索条件**/
    $(document).on("click", ".transfer-search-div .search-clear-btn", function (event) {
        $(this).prev().val("");
        searchData($(this));
    });
    /**获取搜索后的数据  **/
    function searchData($this) {
        var value = $($this).val();
        var $parent = $this.parents(".transfer-content");
        var dds = $parent.find(".transfer-panel-left .transfer-div").find("dd");
        //显示搜索结果菜单
        var k = value;
        var patt = new RegExp(k);
        for (var i = 0; i < dds.length; i++) {
            if (k == "") {
                $(dds[i]).show();
            }
            else if (patt.test($(dds[i]).attr("lay-title"))) {
                $(dds[i]).show();
            }
            else {
                $(dds[i]).hide();
            }
        }

    }

})(jQuery);
$(document).ready(function () {

    //下拉树查询
    var transferTools = $("[cyType='transferTool']");
    for (var i = 0; i < transferTools.length; i++) {
        $(transferTools[i]).transferTool();
    }
});