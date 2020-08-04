
### 分支操作
```dotnetcli
git checkout branchname	//切换到branchname 分支
git checkout -b branchname	//基于当前分支创建branchname分支，并切换

git cherry-pick	mmmHash		//将hash值为mmmHash的commit 的内容合并到当前分支
git merge yyy		//将yyy分支的内容合并到当前分支

git branch -D xxxx  //删除xxxx分支，不会删除远程
git push origin -d xxxx  //删除远程xxxx分支
```

### 同步操作
```dotnetcli
git status 			//当前分支状态
git fetch 			//比较远程仓库代码和代码，如果没冲突，会拉取到本地
git pull            //拉取远程到本地，不管是否有冲突
git add <xxx>		//添加xxx文件准备提交
git add -A			//添加所有的文件准备提交
git commit -m "描述信息"  //提交commit
git push xxxx        //提交xxxx分支到远程，不写xxx 默认代表当前分支
git push  -f  xxx        //强制提交，覆盖远程
```


### tag操作
```dotnetcli
//查看所有tag
git tag　

//创建tag并添加注释
git tag -a tag名字 -m '注释' （注释需要用英文单引号引起来）

 //创建tag，并覆盖已有tag，如果存在同名的话
git tag -f tag名 

//本地删除tag
git tag -d test_tag　

//!!!本地tag推送到线上
git push origin tag名　

//使用git checkout tag即可切换到指定tag，例如：git checkout v0.1.0
git checkout tagName 

//尝试git checkout -b newBranchName tag创建一个基于指定tag的分支
git checkout -b newBranchName v0.2

//强制创建一个基于指定的tag的分支。
git checkout -B test v0.1.0 

PS: 注意大小写。如果要创建的分支名已经存在，大写-B会覆盖掉已经存在的分支，小写的会报警告无法创建
```


## 一些操作细节
默认情况下 git 采用的是vim 快捷操作，简单说明一些长用的按键：
- q键 退出编辑模式，也就是 冒号一闪一闪 的模式
- w建 保存当前内容，