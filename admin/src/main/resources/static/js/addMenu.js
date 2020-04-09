layui.use(['form'], function () {
    window.form = layui.form;
    // 初始化排序下拉选项
    var pid = $(".select-tree").data('value');
    if (pid !== undefined){
        sortRender({id: pid});
    }
});

// 初始化下拉树
$.fn.selectTree({
    rootTree: '顶级菜单',
    // 选中后事件
    onSelected: sortRender
});

// 更新渲染排序下拉选项
function sortRender(treeNode) {
    var pid = treeNode.id;
    var sort = $(".select-sort");
    var id = sort.data('id') ? sort.data('id') : 0;
    var url = sort.data('url') + "/" + pid + "/" + id;
    $.get(url, function (result) {
        var options = '';
        var sortNum = Object.keys(result).length;
        if(pid === $(".select-tree").data('value') && sort.data('sort')){
            sortNum = sort.data('sort') - 1;
        }
        result[0] = "首位";
        for(var key in result){
            var selected = sortNum == key ? "selected=''" : "";
            options += "<option value='"+ key +"' " + selected + ">"+ result[key] +"</option>";
        }
        sort.html(options);
        form.render('select');
    });
}

// 监听变动图标
$(".icon-input").on("input propertychange", function(){
    $(".icon-show").attr("class", "icon-show "+$(this).val());
});

// 同步操作权限输入框
var $perms = $(".perms-input").val();
$(".url-input").on("input propertychange", function(){
    if($perms === ''){
        $(".perms-refresh").click();
    }
});

// 更新权限标识
$(".perms-refresh").on("click", function (e) {
    e.preventDefault();
    var $perms = $(".perms-input");
    var url = $(".url-input").val().substr(1);
    var perms = url.replace(new RegExp( '/' , "g" ), ':');
    $perms.val(perms);
})