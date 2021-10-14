# Ambari-Server

在使用ambari-server安装服务时的一些总结

### 准备环节
- 机器基础环境准备
    - 配置host
    - 配置dns
    - 检查磁盘挂载 xfs/ext4
    - 检查网络端口
    - 配置yum源
        - 需要根据自己公司的定制yum源进行配置，存在不同差异
        - 安装过程中可能会遇到系统版本包与安装版本包不兼容的情况，需要进行降级安装
        - `yum -y downgrade libsmbclint libwbclient --enablerepo=minirepo` `rpm -e sssd-client sssd-common`
         `mv /etc/yum.repo.d/rhn7.repo /etc/yum.repo.d/rhn7.repo.bk`
    - 关闭swap分区
    - 关闭防火墙等
    - umask 0022
    - 安装jdk
    - 配置ssh双向免密
        - 若有root密码权限，做正常的双向免密即可
        - 若没有root密码权限，可以通过共享一份公私钥的方式，将已免密的节点的/root/.ssh/目录，cp至新机器的对应目录下。
        - 并追加`echo "StrictHostKeyChecking no" >> /root/.ssh/config`命令，跳过输入yes的步骤

- 页面安装
    - 根据机器配置对安装/扩容机器进行分组（例如磁盘挂在问题导致的每个机器的磁盘不一致），将机器配置不同的分配至相同的组内
    - 扩容最好先安装client，在安装具体组件，防止keytab生成异常