package cms.web.action.cache;

/**
 * 缓存状态
 *
 */
public class CacheStatus {
	/** 服务器地址 **/
	private String serviceAddress;
	private String memCached_delete_hits;
	/** 当前已使用的内存容量 **/
	private String memCached_bytes;
	/** 服务器本次启动以来，曾存储的Item总个数 **/
	private String memCached_total_items;
	private String memCached_listen_disabled_num;
	private String memCached_auth_errors;
	/** 移除的缓存对象的数目 **/
	private String memCached_evictions;
	/** MemCached服务版本 **/
	private String memCached_version;
	/** MemCached服务器架构 **/
	private String memCached_pointer_size;
	/** 服务器当前时间 **/
	private String memCached_time;
	private String memCached_incr_hits;
	/** 被请求的工作线程的总数量 **/
	private String memCached_threads;
	/** 允许服务支配的最大内存容量 **/
	private String memCached_limit_maxbytes;
	/** 服务器本次启动以来，读取的数据量 **/
	private String memCached_bytes_read;
	/** 当前系统打开的连接数 **/
	private String memCached_curr_connections;
	/** 获取数据失败的次数 **/
	private String memCached_get_misses;
	/** 服务器本次启动以来，写入的数据量 **/
	private String memCached_bytes_written;
	/** 服务器分配的连接结构数 **/
	private String memCached_connection_structures;
	private String memCached_cas_hits;
	private String memCached_delete_misses;
	/** 服务器本次启动以来，累计响应连接总次数 **/
	private String memCached_total_connections;
	private String memCached_cmd_flush;
	/** 服务器本次启动以来，总共运行时间 **/
	private String memCached_uptime;
	/** Memcached服务进程的进程ID **/
	private String memCached_pid;
	private String memCached_cas_badval;
	/** 服务器本次启动以来，Get操作的命中次数 **/
	private String memCached_get_hits;
	/** 当前存储的Item个数 **/
	private String memCached_curr_items;
	private String memCached_cas_misses;
	private String memCached_accepting_conns;
	/** 服务器本次启动以来，执行Get命令总次数 **/
	private String memCached_cmd_get;
	/** 服务器本次启动以来，执行Set命令总次数 **/
	private String memCached_cmd_set;
	private String memCached_incr_misses;
	private String memCached_auth_cmds;
	private String memCached_decr_misses;
	private String memCached_decr_hits;
	private String memCached_conn_yields;
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	public String getMemCached_delete_hits() {
		return memCached_delete_hits;
	}
	public void setMemCached_delete_hits(String memCached_delete_hits) {
		this.memCached_delete_hits = memCached_delete_hits;
	}
	public String getMemCached_bytes() {
		return memCached_bytes;
	}
	public void setMemCached_bytes(String memCached_bytes) {
		this.memCached_bytes = memCached_bytes;
	}
	public String getMemCached_total_items() {
		return memCached_total_items;
	}
	public void setMemCached_total_items(String memCached_total_items) {
		this.memCached_total_items = memCached_total_items;
	}
	public String getMemCached_listen_disabled_num() {
		return memCached_listen_disabled_num;
	}
	public void setMemCached_listen_disabled_num(
			String memCached_listen_disabled_num) {
		this.memCached_listen_disabled_num = memCached_listen_disabled_num;
	}
	public String getMemCached_auth_errors() {
		return memCached_auth_errors;
	}
	public void setMemCached_auth_errors(String memCached_auth_errors) {
		this.memCached_auth_errors = memCached_auth_errors;
	}
	public String getMemCached_evictions() {
		return memCached_evictions;
	}
	public void setMemCached_evictions(String memCached_evictions) {
		this.memCached_evictions = memCached_evictions;
	}
	public String getMemCached_version() {
		return memCached_version;
	}
	public void setMemCached_version(String memCached_version) {
		this.memCached_version = memCached_version;
	}
	public String getMemCached_pointer_size() {
		return memCached_pointer_size;
	}
	public void setMemCached_pointer_size(String memCached_pointer_size) {
		this.memCached_pointer_size = memCached_pointer_size;
	}
	public String getMemCached_time() {
		return memCached_time;
	}
	public void setMemCached_time(String memCached_time) {
		this.memCached_time = memCached_time;
	}
	public String getMemCached_incr_hits() {
		return memCached_incr_hits;
	}
	public void setMemCached_incr_hits(String memCached_incr_hits) {
		this.memCached_incr_hits = memCached_incr_hits;
	}
	public String getMemCached_threads() {
		return memCached_threads;
	}
	public void setMemCached_threads(String memCached_threads) {
		this.memCached_threads = memCached_threads;
	}
	public String getMemCached_limit_maxbytes() {
		return memCached_limit_maxbytes;
	}
	public void setMemCached_limit_maxbytes(String memCached_limit_maxbytes) {
		this.memCached_limit_maxbytes = memCached_limit_maxbytes;
	}
	public String getMemCached_bytes_read() {
		return memCached_bytes_read;
	}
	public void setMemCached_bytes_read(String memCached_bytes_read) {
		this.memCached_bytes_read = memCached_bytes_read;
	}
	public String getMemCached_curr_connections() {
		return memCached_curr_connections;
	}
	public void setMemCached_curr_connections(String memCached_curr_connections) {
		this.memCached_curr_connections = memCached_curr_connections;
	}
	public String getMemCached_get_misses() {
		return memCached_get_misses;
	}
	public void setMemCached_get_misses(String memCached_get_misses) {
		this.memCached_get_misses = memCached_get_misses;
	}
	public String getMemCached_bytes_written() {
		return memCached_bytes_written;
	}
	public void setMemCached_bytes_written(String memCached_bytes_written) {
		this.memCached_bytes_written = memCached_bytes_written;
	}
	public String getMemCached_connection_structures() {
		return memCached_connection_structures;
	}
	public void setMemCached_connection_structures(
			String memCached_connection_structures) {
		this.memCached_connection_structures = memCached_connection_structures;
	}
	public String getMemCached_cas_hits() {
		return memCached_cas_hits;
	}
	public void setMemCached_cas_hits(String memCached_cas_hits) {
		this.memCached_cas_hits = memCached_cas_hits;
	}
	public String getMemCached_delete_misses() {
		return memCached_delete_misses;
	}
	public void setMemCached_delete_misses(String memCached_delete_misses) {
		this.memCached_delete_misses = memCached_delete_misses;
	}
	public String getMemCached_total_connections() {
		return memCached_total_connections;
	}
	public void setMemCached_total_connections(String memCached_total_connections) {
		this.memCached_total_connections = memCached_total_connections;
	}
	public String getMemCached_cmd_flush() {
		return memCached_cmd_flush;
	}
	public void setMemCached_cmd_flush(String memCached_cmd_flush) {
		this.memCached_cmd_flush = memCached_cmd_flush;
	}
	public String getMemCached_uptime() {
		return memCached_uptime;
	}
	public void setMemCached_uptime(String memCached_uptime) {
		this.memCached_uptime = memCached_uptime;
	}
	public String getMemCached_pid() {
		return memCached_pid;
	}
	public void setMemCached_pid(String memCached_pid) {
		this.memCached_pid = memCached_pid;
	}
	public String getMemCached_cas_badval() {
		return memCached_cas_badval;
	}
	public void setMemCached_cas_badval(String memCached_cas_badval) {
		this.memCached_cas_badval = memCached_cas_badval;
	}
	public String getMemCached_get_hits() {
		return memCached_get_hits;
	}
	public void setMemCached_get_hits(String memCached_get_hits) {
		this.memCached_get_hits = memCached_get_hits;
	}
	public String getMemCached_curr_items() {
		return memCached_curr_items;
	}
	public void setMemCached_curr_items(String memCached_curr_items) {
		this.memCached_curr_items = memCached_curr_items;
	}
	public String getMemCached_cas_misses() {
		return memCached_cas_misses;
	}
	public void setMemCached_cas_misses(String memCached_cas_misses) {
		this.memCached_cas_misses = memCached_cas_misses;
	}
	public String getMemCached_accepting_conns() {
		return memCached_accepting_conns;
	}
	public void setMemCached_accepting_conns(String memCached_accepting_conns) {
		this.memCached_accepting_conns = memCached_accepting_conns;
	}
	public String getMemCached_cmd_get() {
		return memCached_cmd_get;
	}
	public void setMemCached_cmd_get(String memCached_cmd_get) {
		this.memCached_cmd_get = memCached_cmd_get;
	}
	public String getMemCached_cmd_set() {
		return memCached_cmd_set;
	}
	public void setMemCached_cmd_set(String memCached_cmd_set) {
		this.memCached_cmd_set = memCached_cmd_set;
	}
	public String getMemCached_incr_misses() {
		return memCached_incr_misses;
	}
	public void setMemCached_incr_misses(String memCached_incr_misses) {
		this.memCached_incr_misses = memCached_incr_misses;
	}
	public String getMemCached_auth_cmds() {
		return memCached_auth_cmds;
	}
	public void setMemCached_auth_cmds(String memCached_auth_cmds) {
		this.memCached_auth_cmds = memCached_auth_cmds;
	}
	public String getMemCached_decr_misses() {
		return memCached_decr_misses;
	}
	public void setMemCached_decr_misses(String memCached_decr_misses) {
		this.memCached_decr_misses = memCached_decr_misses;
	}
	public String getMemCached_decr_hits() {
		return memCached_decr_hits;
	}
	public void setMemCached_decr_hits(String memCached_decr_hits) {
		this.memCached_decr_hits = memCached_decr_hits;
	}
	public String getMemCached_conn_yields() {
		return memCached_conn_yields;
	}
	public void setMemCached_conn_yields(String memCached_conn_yields) {
		this.memCached_conn_yields = memCached_conn_yields;
	}
	
	
	
	
	
	
	
	
	
}
