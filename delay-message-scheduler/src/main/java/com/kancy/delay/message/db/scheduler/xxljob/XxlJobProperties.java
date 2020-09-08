package com.kancy.delay.message.db.scheduler.xxljob;

/**
 * <p>
 * XxlJobProperties
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 11:05
 **/
public class XxlJobProperties {
    /**
     * Admin调度平台配置
     */
    private Admin admin = new Admin();

    /**
     * 执行器
     */
    private Executor executor = new Executor();

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static class Admin {
        /**
         * Admin调度平台地址
         */
        private String addresses;

        /**
         * Admin调度平台访问Token
         */
        private String accessToken;

        /**
         * 日志保留天数
         */
        private int logretentiondays;

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getLogretentiondays() {
            return logretentiondays;
        }

        public void setLogretentiondays(int logretentiondays) {
            this.logretentiondays = logretentiondays;
        }
    }

    public static class Executor {
        /**
         * 执行器名称
         */
        private String appname;
        /**
         * 执行器IP
         */
        private String ip;
        /**
         * 执行器端口
         */
        private int port;
        /**
         * 执行日志路径
         */
        private String logpath;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getLogpath() {
            return logpath;
        }

        public void setLogpath(String logpath) {
            this.logpath = logpath;
        }
    }
}
