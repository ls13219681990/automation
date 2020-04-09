// 树形数据展示
;(function ($) {
    var self;
    var TableTree = function (param) {
        self = this;
        this.defaults = {
            tree: $(".timo-tree"),
            treeTable: ".timo-tree-table",   // 表格类名
            treeIcon: "<i class='toggle-icon fa fa-chevron-right'></i>",
            treeFill: "<i class='toggle-fill'></i>",
            hideRank: 3,
            oldActive: null,
            oldButton: null,
            scrollTop: 90,
        }
        this.options = $.extend({}, this.defaults, param);
    }

    TableTree.prototype = {
        // 初始化
        init: function () {
            // 获取树形列表数据
            var tree = self.options.tree;
            $.get(tree.data('url'), function (result) {
                if (result.data.length > 0) {
                    // zTree传递列表数据
                    self.zTreeReady(result.data);
                    // 树形表格传递列表数据
                    self.tableTree(result.data);
                    // 开启树形表格子级开关
                    self.toggleChild();
                }
            });
        },

        // 操作zTree组件
        zTreeReady: function (listData) {
            var setting = {
                view: {
                    addHoverDom: addHoverDom,
                    removeHoverDom: removeHoverDom,
                }, edit: {
                    enable: true,
                    showRenameBtn: false
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: onClick,
                    onExpand: onExpand,
                    onCollapse: onCollapse,
                    //beforeEditName: beforeEditName, 点击编辑时触发，用来判断该节点是否能编辑,是否进入编辑状态
                    beforeRemove: beforeRemove//点击删除时触发，用来提示用户是否确定删除
                }
            };

            function onClick(event, treeId, treeNode, clickFlag) {


                var url = treeNode.url.substring(0, treeNode.url.indexOf("="));//截取字符串
                var tNode = $("[tree-id='" + treeNode.id + "']");
                if (self.options.treeActive != null) {
                    self.options.treeActive.removeClass("tree-active");
                }
                self.options.treeActive = tNode;
                tNode.addClass("tree-active");

                nodeInfo = name;
                //var url = treeNode.url+"Manage"+"?nodeName="+treeNode.name;
                document.getElementById("iframe").src = url + "=" + treeNode.name;

                /*var src = $(document).getElementById("iframe").value;*/
                /*
                                $(document).getElementById("iframe").src=' th:src="@｛wwww.baidu.com｝';
                                $(document).scrollTop(tNode.offset().top - self.options.scrollTop);*/

                /*document.getElementById("iframe").src=treeNode.url;*/

            }

            function onExpand(event, treeId, treeNode) {
                var tNode = $("[tree-id='" + treeNode.id + "']");
                self.expandChild(tNode, true);
            }

            function onCollapse(event, treeId, treeNode) {
                var tNode = $("[tree-id='" + treeNode.id + "']");
                self.expandChild(tNode, false);
            }


            function addHoverDom(treeId, treeNode) {
                var node = $("#" + treeNode.tId + "_span");
                if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) return;
                var addNode = "<span class='button tree-add' id='addBtn_" + treeNode.tId + "'></span>";

                node.after(addNode);
                var btn = $("#addBtn_" + treeNode.tId);
                if (btn) btn.bind("click", function () {


                    var popup = $(".add-line");
                    var Arrkey, reg = /^jQuery[0-9]\d*$/
                    for (var key in popup[0]) {
                        if (reg.test(key)) {
                            Arrkey = key
                        }
                    }

                    url = popup.data('url');

                    if (treeNode.engineering == 1) {
                        var array = treeNode.pids;
                        var pid = treeNode.pId;
                        if (pid == null) {
                            pid = 0;
                        }

                        array = array.replace(/\[|]/g, '')
                        array = array.split(",")

                        for (var i = 0; i < array.length; i++) {
                            if (array[i] === pid.toString())
                                var index = i;
                        }

                        var falg = true;

                        if (index == 0) {
                            //url = "/system/menu/addLineInfo";//线路
                            url = "/system/menu/addBidSection"//标段
                            popup.attr("data-title", '添加标段');
                            popup[0].attributes.getNamedItem('data-size').value = '770,425'
                        } else if (index == 1) {
                            url = "/system/menu/addWorkSpot"//工点
                            popup.attr("data-title", '添加工点');
                            popup[0].attributes.getNamedItem('data-size').value = '770,340'
                        } else if (index == 2) {
                            url = "/system/menu/addMeasuringSpot"//测点
                            popup.attr("data-title", '添加测点');
                            popup[0].attributes.getNamedItem('data-size').value = '1130,540'
                        } else {
                            falg = false;
                        }
                    }
                    /*if (Arrkey != undefined && Arrkey != null) {
                        if (index == 0) {
                            popup[0][Arrkey].title = '添加标段';
                        } else if (index == 1) {
                            popup[0][Arrkey].title = '添加工点';
                        } else if (index == 2) {
                            popup[0][Arrkey].title = '添加测点';
                        }

                    }*/
                    if (Arrkey != undefined && Arrkey != null) {
                        delete popup[0][Arrkey].title;
                    }
                    if (falg) {
                        popup.attr("data-url", url + "/" + treeNode.id);
                        popup.click();
                        /*url = "/system/menu/addLineInfo";
                        popup.attr("data-url", url);*/
                        /* popup.attr("data-title", '添加线路');
                         popup[0].attributes.getNamedItem('data-size').value = '750,510'*/
                    }
                    return false;
                });
            }


            function removeHoverDom(treeId, treeNode) {
                $("#addBtn_" + treeNode.tId).unbind().remove();
            }

            /*function beforeEditName(treeId, treeNode) {
                var trNode = $("[tree-id='" + treeNode.id + "']");
                var edit = trNode.find(".popup-edit");
                edit.click();

                return false;
            }*/


            function beforeRemove(treeId, treeNode) {
                /* var trNode = $("[tree-id='" + treeNode.id + "']");
                 var del = trNode.find(".popup-delete");*/
                var typeId = treeNode.id;
                if (confirm("确定删除该节点下面的所有节点?")) {
                    $.post("/system/menu/delTree", {"id": typeId}, function (reposonse) {

                        if (reposonse.code == 200) {
                            alert(reposonse.msg);
                        } else {
                            alert("删除失败");
                        }
                    })
                }


                /*del.click();*/
                return false;
            }

            // 封装zTree数据
            var zNodes = [];
            listData.forEach(function (val) {
                var nav = {
                    id: val.id,
                    pId: val.pid,
                    name: val.title,
                    pids: val.pids,
                    engineering: val.engineering,
                    url: val.url

                };
                if (nav.pId == 0) {
                    nav.isParent = true;
                    nav.open = true;
                }
                zNodes.push(nav);
            });

            // zTree组件初始化
            $(document).ready(function () {
                $.fn.zTree.init(self.options.tree.find(".ztree"), setting, zNodes);
               /* var treeObj = $.fn.zTree.getZTreeObj(".ztree");
                var nodes = treeObj.getNodesByParam("title", "测试测点", null);
                console.log(1)*/

                var zTree=$.fn.zTree.getZTreeObj("ztree");
                var nodes = zTree.getNodesByParamFuzzy("id", nodeId, null);
                console.log(nodes);
                if (nodes.length>0) {
                    zTree.selectNode(nodes[0]);
                }  

            });
        },


        // 封装树形表格
        tableTree: function (listData) {
            // 封装树形结构数据
            var newList = [];
            var treeList = [];

            listData.forEach(function (item) {
                newList[item.id] = item;
            });

            listData.forEach(function (item) {
                if (newList[item.pid] != undefined) {
                    if (newList[item.pid].children == undefined) {
                        newList[item.pid].children = [];
                    }
                    newList[item.pid].children.push(item);
                } else {
                    treeList.push(item);
                }
            });

            // 获取表格展示模型
            var tbody = $(self.options.treeTable + " tbody");
            var template = "\n <tr class=\"{{$hide}}\" tree-pid=\"{{pid}}\" tree-id=\"{{id}}\">\n <td><label class=\"timo-checkbox\"><input type=\"checkbox\" value=\"{{id}}\">\n <i class=\"layui-icon layui-icon-ok\"></i></label></td>\n <td>{{title}}</td>\n <td>{{url}}</td>\n <td>{{perms}}</td>\n <td>{{remark}}</td>\n <td>正常</td>\n <td>\n <a class=\"open-popup popup-edit\" data-title=\"编辑菜单\" href=\"#\" data-url=\"/system/menu/edit/{{id}}\">编辑</a>\n <a class=\"open-popup\" data-title=\"详细信息\" data-size=\"800,600\" href=\"#\" data-url=\"/system/menu/detail/{{id}}\">详细</a>\n <a class=\"ajax-get popup-delete\" href=\"/system/menu/status/delete?ids={{id}}\" data-msg=\"您是否确定删除\">删除</a>\n </td>\n </tr>\n ";
            /* var template = tbody.html();*/

            tbody.empty();
            tbody.css("visibility", "visible");
            // 填充数据
            var regex = /\{\{([$A-Za-z]+?)\}\}/g;
            var rank = 1;
            self.expandTree(treeList, rank, function (item, rank) {
                var callback = template.replace(regex, function ($1) {
                    var point = $1.substring(2, $1.length - 2);
                    if (point == "title") {
                        var icon = self.options.treeIcon;
                        if (item.children == undefined) {
                            icon = self.options.treeFill;
                        }
                        var fill = "";
                        for (var i = 0; i < rank - 1; i++) {
                            fill += self.options.treeFill;
                        }
                        return fill + icon + item[point];
                    } else if (point == '$hide') {
                        var isHide = (rank >= self.options.hideRank);
                        return isHide ? "tree-hidd" : "";
                    } else {
                        return item[point];
                    }
                });
                tbody.append(callback);
            });
        },


        // 展开树形数据
        expandTree: function (list, rank, callback) {
            list.forEach(function (item) {
                callback(item, rank);
                if (item.children != undefined) {
                    self.expandTree(item.children, ++rank, callback);
                    rank -= 1;
                }
            });
        },

        // 树形表格子级开关
        toggleChild: function () {
            $(".toggle-icon").click(function () {
                var trNode = $(this).parents("tr");
                var id = trNode.attr("tree-id");
                var childs = $("[tree-pid='" + id + "']");
                var isClose = childs.hasClass("tree-hidd");
                self.expandChild(trNode, isClose);
            });
        },

        // 递归所有子级开关
        expandChild: function (trNode, isClose) {
            var id = trNode.attr("tree-id");
            var childs = $("[tree-pid='" + id + "']");
            if (!isClose) {
                childs.addClass("tree-hidd");
                childs.each(function (key, item) {
                    self.expandChild($(item), isClose);
                });
            } else {
                childs.removeClass("tree-hidd");
            }
        }
    }

    $.fn.engineeringTree = function (param) {
        var tableTree = new TableTree(param);
        return tableTree.init();
    }
})(jQuery);

// 树形选择器
(function ($) {
    var self;
    var SelectTree = function (param) {
        self = this;
        this.defaults = {
            tree: $(".select-tree"),
            rootTree: null,
            onSelected: function () {
            }
        }
        this.options = $.extend({}, this.defaults, param);
    }

    SelectTree.prototype = {
        // 初始化
        init: function () {
            // 获取树形列表数据
            var tree = self.options.tree;
            // 构造悬浮选择器
            self.selector();
            // 重构选择框
            self.resetSelect(tree);
            // 点击时显示悬浮选择器
            tree.click(function () {
                var node = $(this);
                $.get(node.data('url'), function (result) {
                    //if(result.data.length > 0){
                    // 显示定位悬浮选择器
                    self.position(node);
                    // zTree传递列表数据
                    self.zTreeReady(result.data, node);
                    //}
                });
            });
        },

        // 操作zTree组件
        zTreeReady: function (listData, node) {
            var setting = {
                view: {
                    dblClickExpand: false
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: function (event, treeId, treeNode) {
                        node.val(treeNode.name);
                        node.siblings("[type='hidden']").val(treeNode.id);
                        $(".selectContent").hide();
                        self.options.onSelected(treeNode);
                    }
                }
            };

            // 封装zTree数据
            var zNodes = [];
            if (self.options.rootTree != null) {
                zNodes.push({id: 0, name: self.options.rootTree, open: true});
            }
            listData.forEach(function (val) {
                var nav = {
                    id: val.id,
                    pId: val.pid,
                    name: val.title,
                    engineering: val.engineering,
                    url: val.url
                };
                if (nav.pId == 0) {
                    nav.isParent = true;
                    nav.open = true;
                }
                zNodes.push(nav);
            });

            $(document).ready(function () {
                $.fn.zTree.init($(".selectContent>.ztree"), setting, zNodes);
            });
        },

        // 构造悬浮选择器
        selector: function () {
            $("body").append("\n" +
                "<div class='selectContent'>" +
                "    <ul class='ztree'></ul>" +
                "</div>");
        },

        // 重构选择框
        resetSelect: function (tree) {
            tree.each(function (key, item) {
                var name = $(item).attr("name");
                var value = $(item).data("value");
                $(item).removeAttr("name");
                $(item).attr("readonly", true);
                var input = $("<input name='" + name + "' type='hidden'>");
                if (value != undefined) input.val(value);
                $(item).after(input);
                $(item).after("<i class='layui-edge'></i>");
            });
        },

        // 显示定位悬浮选择器
        position: function (tree) {
            var source = self.options.tree;
            var offset = tree.offset();
            $(".selectContent").css({
                top: offset.top + tree.outerHeight() + 'px',
                left: offset.left + 'px',
                width: source.innerWidth()
            }).show();

            $("body").bind("click", function (e) {
                var target = $(e.target).parents(".selectContent");
                if (!target.length > 0) {
                    $(".selectContent").hide();
                }
            });
        },
    }

    $.fn.selectTree = function (param) {
        var selectTree = new SelectTree(param);
        return selectTree.init();
    }
})(jQuery);


