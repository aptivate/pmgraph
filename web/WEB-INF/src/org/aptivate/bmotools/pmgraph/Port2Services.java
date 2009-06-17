package org.aptivate.bmotools.pmgraph;

import java.util.HashMap;

/**
 * 
 * 
 * @author noeg
 * 
 * List of maps between ports and services using de command
 * 
 * sed "s/^\([a-z0-9\-]\+\)[ \t]\+\([0-9]\{4,5\}\)\/tcp.*$/m_tcpPort2serviceMap\.put(\2, \"\1\");/gi"
 * /etc/services | grep "\.put"
 * 
 * in a linux system
 */

public class Port2Services
{
	private static Port2Services m_instance;

	// UDP port services translation
	private final HashMap<Integer, String> m_udpPort2serviceMap = new HashMap<Integer, String>();
	{
		m_udpPort2serviceMap.put(1, "tcpmux");
		m_udpPort2serviceMap.put(5, "rje");
		m_udpPort2serviceMap.put(7, "echo");
		m_udpPort2serviceMap.put(9, "discard");
		m_udpPort2serviceMap.put(11, "systat");
		m_udpPort2serviceMap.put(13, "daytime");
		m_udpPort2serviceMap.put(17, "qotd");
		m_udpPort2serviceMap.put(18, "msp");
		m_udpPort2serviceMap.put(19, "chargen");
		m_udpPort2serviceMap.put(20, "ftp-data");
		m_udpPort2serviceMap.put(21, "ftp");
		m_udpPort2serviceMap.put(22, "ssh");
		m_udpPort2serviceMap.put(23, "telnet");
		m_udpPort2serviceMap.put(25, "smtp");
		m_udpPort2serviceMap.put(37, "time");
		m_udpPort2serviceMap.put(39, "rlp");
		m_udpPort2serviceMap.put(42, "nameserver");
		m_udpPort2serviceMap.put(43, "nicname");
		m_udpPort2serviceMap.put(49, "tacacs");
		m_udpPort2serviceMap.put(50, "re-mail-ck");
		m_udpPort2serviceMap.put(53, "domain");
		m_udpPort2serviceMap.put(67, "bootps");
		m_udpPort2serviceMap.put(68, "bootpc");
		m_udpPort2serviceMap.put(69, "tftp");
		m_udpPort2serviceMap.put(70, "gopher");
		m_udpPort2serviceMap.put(71, "netrjs-1");
		m_udpPort2serviceMap.put(72, "netrjs-2");
		m_udpPort2serviceMap.put(73, "netrjs-3");
		m_udpPort2serviceMap.put(74, "netrjs-4");
		m_udpPort2serviceMap.put(79, "finger");
		m_udpPort2serviceMap.put(80, "http");
		m_udpPort2serviceMap.put(88, "kerberos");
		m_udpPort2serviceMap.put(95, "supdup");
		m_udpPort2serviceMap.put(101, "hostname");
		m_udpPort2serviceMap.put(105, "csnet-ns");
		m_udpPort2serviceMap.put(107, "rtelnet");
		m_udpPort2serviceMap.put(109, "pop2");
		m_udpPort2serviceMap.put(110, "pop3");
		m_udpPort2serviceMap.put(111, "sunrpc");
		m_udpPort2serviceMap.put(113, "auth");
		m_udpPort2serviceMap.put(115, "sftp");
		m_udpPort2serviceMap.put(117, "uucp-path");
		m_udpPort2serviceMap.put(119, "nntp");
		m_udpPort2serviceMap.put(123, "ntp");
		m_udpPort2serviceMap.put(137, "netbios-ns");
		m_udpPort2serviceMap.put(138, "netbios-dgm");
		m_udpPort2serviceMap.put(139, "netbios-ssn");
		m_udpPort2serviceMap.put(143, "imap");
		m_udpPort2serviceMap.put(161, "snmp");
		m_udpPort2serviceMap.put(162, "snmptrap");
		m_udpPort2serviceMap.put(163, "cmip-man");
		m_udpPort2serviceMap.put(164, "cmip-agent");
		m_udpPort2serviceMap.put(174, "mailq");
		m_udpPort2serviceMap.put(177, "xdmcp");
		m_udpPort2serviceMap.put(178, "nextstep");
		m_udpPort2serviceMap.put(179, "bgp");
		m_udpPort2serviceMap.put(191, "prospero");
		m_udpPort2serviceMap.put(194, "irc");
		m_udpPort2serviceMap.put(199, "smux");
		m_udpPort2serviceMap.put(201, "at-rtmp");
		m_udpPort2serviceMap.put(202, "at-nbp");
		m_udpPort2serviceMap.put(204, "at-echo");
		m_udpPort2serviceMap.put(206, "at-zis");
		m_udpPort2serviceMap.put(209, "qmtp");
		m_udpPort2serviceMap.put(213, "ipx");
		m_udpPort2serviceMap.put(220, "imap3");
		m_udpPort2serviceMap.put(245, "link");
		m_udpPort2serviceMap.put(347, "fatserv");
		m_udpPort2serviceMap.put(369, "rpc2portmap");
		m_udpPort2serviceMap.put(370, "codaauth2");
		m_udpPort2serviceMap.put(372, "ulistproc");
		m_udpPort2serviceMap.put(389, "ldap");
		m_udpPort2serviceMap.put(427, "svrloc");
		m_udpPort2serviceMap.put(434, "mobileip-agent");
		m_udpPort2serviceMap.put(435, "mobilip-mn");
		m_udpPort2serviceMap.put(443, "https");
		m_udpPort2serviceMap.put(444, "snpp");
		m_udpPort2serviceMap.put(445, "microsoft-ds");
		m_udpPort2serviceMap.put(464, "kpasswd");
		m_udpPort2serviceMap.put(468, "photuris");
		m_udpPort2serviceMap.put(487, "saft");
		m_udpPort2serviceMap.put(488, "gss-http");
		m_udpPort2serviceMap.put(496, "pim-rp-disc");
		m_udpPort2serviceMap.put(500, "isakmp");
		m_udpPort2serviceMap.put(538, "gdomap");
		m_udpPort2serviceMap.put(535, "iiop");
		m_udpPort2serviceMap.put(546, "dhcpv6-client");
		m_udpPort2serviceMap.put(547, "dhcpv6-server");
		m_udpPort2serviceMap.put(554, "rtsp");
		m_udpPort2serviceMap.put(563, "nntps");
		m_udpPort2serviceMap.put(565, "whoami");
		m_udpPort2serviceMap.put(587, "submission");
		m_udpPort2serviceMap.put(610, "npmp-local");
		m_udpPort2serviceMap.put(611, "npmp-gui");
		m_udpPort2serviceMap.put(612, "hmmp-ind");
		m_udpPort2serviceMap.put(631, "ipp");
		m_udpPort2serviceMap.put(636, "ldaps");
		m_udpPort2serviceMap.put(674, "acap");
		m_udpPort2serviceMap.put(694, "ha-cluster");
		m_udpPort2serviceMap.put(750, "kerberos-iv");
		m_udpPort2serviceMap.put(765, "webster");
		m_udpPort2serviceMap.put(767, "phonebook");
		m_udpPort2serviceMap.put(873, "rsync");
		m_udpPort2serviceMap.put(992, "telnets");
		m_udpPort2serviceMap.put(993, "imaps");
		m_udpPort2serviceMap.put(994, "ircs");
		m_udpPort2serviceMap.put(995, "pop3s");
		m_udpPort2serviceMap.put(512, "biff");
		m_udpPort2serviceMap.put(513, "who");
		m_udpPort2serviceMap.put(514, "syslog");
		m_udpPort2serviceMap.put(515, "printer");
		m_udpPort2serviceMap.put(517, "talk");
		m_udpPort2serviceMap.put(518, "ntalk");
		m_udpPort2serviceMap.put(519, "utime");
		m_udpPort2serviceMap.put(520, "router");
		m_udpPort2serviceMap.put(521, "ripng");
		m_udpPort2serviceMap.put(525, "timed");
		m_udpPort2serviceMap.put(533, "netwall");
		m_udpPort2serviceMap.put(548, "afpovertcp");
		m_udpPort2serviceMap.put(1080, "socks");
		m_udpPort2serviceMap.put(1236, "bvcontrol");
		m_udpPort2serviceMap.put(1300, "h323hostcallsc");
		m_udpPort2serviceMap.put(1433, "ms-sql-s");
		m_udpPort2serviceMap.put(1434, "ms-sql-m");
		m_udpPort2serviceMap.put(1494, "ica");
		m_udpPort2serviceMap.put(1512, "wins");
		m_udpPort2serviceMap.put(1524, "ingreslock");
		m_udpPort2serviceMap.put(1525, "prospero-np");
		m_udpPort2serviceMap.put(1645, "datametrics");
		m_udpPort2serviceMap.put(1646, "sa-msg-port");
		m_udpPort2serviceMap.put(1649, "kermit");
		m_udpPort2serviceMap.put(1701, "l2tp");
		m_udpPort2serviceMap.put(1718, "h323gatedisc");
		m_udpPort2serviceMap.put(1719, "h323gatestat");
		m_udpPort2serviceMap.put(1720, "h323hostcall");
		m_udpPort2serviceMap.put(1758, "tftp-mcast");
		m_udpPort2serviceMap.put(1759, "mtftp");
		m_udpPort2serviceMap.put(1789, "hello");
		m_udpPort2serviceMap.put(1812, "radius");
		m_udpPort2serviceMap.put(1813, "radius-acct");
		m_udpPort2serviceMap.put(1911, "mtp");
		m_udpPort2serviceMap.put(1985, "hsrp");
		m_udpPort2serviceMap.put(1986, "licensedaemon");
		m_udpPort2serviceMap.put(1997, "gdp-port");
		m_udpPort2serviceMap.put(2049, "nfs");
		m_udpPort2serviceMap.put(2102, "zephyr-srv");
		m_udpPort2serviceMap.put(2103, "zephyr-clt");
		m_udpPort2serviceMap.put(2104, "zephyr-hm");
		m_udpPort2serviceMap.put(2401, "cvspserver");
		m_udpPort2serviceMap.put(2430, "venus");
		m_udpPort2serviceMap.put(2431, "venus-se");
		m_udpPort2serviceMap.put(2432, "codasrv");
		m_udpPort2serviceMap.put(2433, "codasrv-se");
		m_udpPort2serviceMap.put(2600, "hpstgmgr");
		m_udpPort2serviceMap.put(2601, "discp-client");
		m_udpPort2serviceMap.put(2602, "discp-server");
		m_udpPort2serviceMap.put(2603, "servicemeter");
		m_udpPort2serviceMap.put(2604, "nsc-ccs");
		m_udpPort2serviceMap.put(2605, "nsc-posa");
		m_udpPort2serviceMap.put(2606, "netmon");
		m_udpPort2serviceMap.put(3130, "icpv2");
		m_udpPort2serviceMap.put(3306, "mysql");
		m_udpPort2serviceMap.put(3346, "trnsprntproxy");
		m_udpPort2serviceMap.put(4011, "pxe");
		m_udpPort2serviceMap.put(4321, "rwhois");
		m_udpPort2serviceMap.put(4444, "krb524");
		m_udpPort2serviceMap.put(5002, "rfe");
		m_udpPort2serviceMap.put(5308, "cfengine");
		m_udpPort2serviceMap.put(5999, "cvsup");
		m_udpPort2serviceMap.put(7000, "afs3-fileserver");
		m_udpPort2serviceMap.put(7001, "afs3-callback");
		m_udpPort2serviceMap.put(7002, "afs3-prserver");
		m_udpPort2serviceMap.put(7003, "afs3-vlserver");
		m_udpPort2serviceMap.put(7004, "afs3-kaserver");
		m_udpPort2serviceMap.put(7005, "afs3-volser");
		m_udpPort2serviceMap.put(7006, "afs3-errors");
		m_udpPort2serviceMap.put(7007, "afs3-bos");
		m_udpPort2serviceMap.put(7008, "afs3-update");
		m_udpPort2serviceMap.put(7009, "afs3-rmtsys");
		m_udpPort2serviceMap.put(9876, "sd");
		m_udpPort2serviceMap.put(10080, "amanda");
		m_udpPort2serviceMap.put(11371, "pgpkeyserver");
		m_udpPort2serviceMap.put(11720, "h323callsigalt");
		m_udpPort2serviceMap.put(13720, "bprd");
		m_udpPort2serviceMap.put(13721, "bpdbm");
		m_udpPort2serviceMap.put(13722, "bpjava-msvc");
		m_udpPort2serviceMap.put(13724, "vnetd");
		m_udpPort2serviceMap.put(13782, "bpcd");
		m_udpPort2serviceMap.put(13783, "vopied");
		m_udpPort2serviceMap.put(22273, "wnn6");
		m_udpPort2serviceMap.put(26000, "quake");
		m_udpPort2serviceMap.put(26208, "wnn6-ds");
		m_udpPort2serviceMap.put(33434, "traceroute");
		m_udpPort2serviceMap.put(106, "poppassd");
		m_udpPort2serviceMap.put(808, "omirr");
		m_udpPort2serviceMap.put(953, "rndc");
		m_udpPort2serviceMap.put(2150, "ninstall");
		m_udpPort2serviceMap.put(2988, "afbackup");
		m_udpPort2serviceMap.put(3455, "prsvp");
		m_udpPort2serviceMap.put(5432, "postgres");
		m_udpPort2serviceMap.put(5232, "sgi-dgl");
		m_udpPort2serviceMap.put(5354, "noclog");
		m_udpPort2serviceMap.put(5355, "hostmon");
		m_udpPort2serviceMap.put(6667, "ircd");
		m_udpPort2serviceMap.put(8008, "http-alt");
		m_udpPort2serviceMap.put(8080, "webcache");
		m_udpPort2serviceMap.put(8081, "tproxy");
		m_udpPort2serviceMap.put(9359, "mandelspawn");
		m_udpPort2serviceMap.put(10081, "kamanda");
		m_udpPort2serviceMap.put(20011, "isdnlog");
		m_udpPort2serviceMap.put(20012, "vboxd");
		m_udpPort2serviceMap.put(24554, "binkp");
		m_udpPort2serviceMap.put(27374, "asp");
		m_udpPort2serviceMap.put(60177, "tfido");
		m_udpPort2serviceMap.put(60179, "fido");
	}

	// TCP port services translation
	private final HashMap<Integer, String> m_tcpPort2serviceMap = new HashMap<Integer, String>();
	{
		m_tcpPort2serviceMap.put(1, "tcpmux");
		m_tcpPort2serviceMap.put(5, "rje");
		m_tcpPort2serviceMap.put(7, "echo");
		m_tcpPort2serviceMap.put(9, "discard");
		m_tcpPort2serviceMap.put(11, "systat");
		m_tcpPort2serviceMap.put(13, "daytime");
		m_tcpPort2serviceMap.put(17, "qotd");
		m_tcpPort2serviceMap.put(18, "msp");
		m_tcpPort2serviceMap.put(19, "chargen");
		m_tcpPort2serviceMap.put(20, "ftp-data");
		m_tcpPort2serviceMap.put(21, "ftp");
		m_tcpPort2serviceMap.put(22, "ssh");
		m_tcpPort2serviceMap.put(23, "telnet");
		m_tcpPort2serviceMap.put(25, "smtp");
		m_tcpPort2serviceMap.put(37, "time");
		m_tcpPort2serviceMap.put(39, "rlp");
		m_tcpPort2serviceMap.put(42, "nameserver");
		m_tcpPort2serviceMap.put(43, "nicname");
		m_tcpPort2serviceMap.put(49, "tacacs");
		m_tcpPort2serviceMap.put(50, "re-mail-ck");
		m_tcpPort2serviceMap.put(53, "domain");
		m_tcpPort2serviceMap.put(67, "bootps");
		m_tcpPort2serviceMap.put(68, "bootpc");
		m_tcpPort2serviceMap.put(69, "tftp");
		m_tcpPort2serviceMap.put(70, "gopher");
		m_tcpPort2serviceMap.put(71, "netrjs-1");
		m_tcpPort2serviceMap.put(72, "netrjs-2");
		m_tcpPort2serviceMap.put(73, "netrjs-3");
		m_tcpPort2serviceMap.put(74, "netrjs-4");
		m_tcpPort2serviceMap.put(79, "finger");
		m_tcpPort2serviceMap.put(80, "http");
		m_tcpPort2serviceMap.put(88, "kerberos");
		m_tcpPort2serviceMap.put(95, "supdup");
		m_tcpPort2serviceMap.put(101, "hostname");
		m_tcpPort2serviceMap.put(102, "iso-tsap");
		m_tcpPort2serviceMap.put(105, "csnet-ns");
		m_tcpPort2serviceMap.put(107, "rtelnet");
		m_tcpPort2serviceMap.put(109, "pop2");
		m_tcpPort2serviceMap.put(110, "pop3");
		m_tcpPort2serviceMap.put(111, "sunrpc");
		m_tcpPort2serviceMap.put(113, "auth");
		m_tcpPort2serviceMap.put(115, "sftp");
		m_tcpPort2serviceMap.put(117, "uucp-path");
		m_tcpPort2serviceMap.put(119, "nntp");
		m_tcpPort2serviceMap.put(123, "ntp");
		m_tcpPort2serviceMap.put(137, "netbios-ns");
		m_tcpPort2serviceMap.put(138, "netbios-dgm");
		m_tcpPort2serviceMap.put(139, "netbios-ssn");
		m_tcpPort2serviceMap.put(143, "imap");
		m_tcpPort2serviceMap.put(161, "snmp");
		m_tcpPort2serviceMap.put(163, "cmip-man");
		m_tcpPort2serviceMap.put(164, "cmip-agent");
		m_tcpPort2serviceMap.put(174, "mailq");
		m_tcpPort2serviceMap.put(177, "xdmcp");
		m_tcpPort2serviceMap.put(178, "nextstep");
		m_tcpPort2serviceMap.put(179, "bgp");
		m_tcpPort2serviceMap.put(191, "prospero");
		m_tcpPort2serviceMap.put(194, "irc");
		m_tcpPort2serviceMap.put(199, "smux");
		m_tcpPort2serviceMap.put(201, "at-rtmp");
		m_tcpPort2serviceMap.put(202, "at-nbp");
		m_tcpPort2serviceMap.put(204, "at-echo");
		m_tcpPort2serviceMap.put(206, "at-zis");
		m_tcpPort2serviceMap.put(209, "qmtp");
		m_tcpPort2serviceMap.put(213, "ipx");
		m_tcpPort2serviceMap.put(220, "imap3");
		m_tcpPort2serviceMap.put(245, "link");
		m_tcpPort2serviceMap.put(347, "fatserv");
		m_tcpPort2serviceMap.put(369, "rpc2portmap");
		m_tcpPort2serviceMap.put(370, "codaauth2");
		m_tcpPort2serviceMap.put(372, "ulistproc");
		m_tcpPort2serviceMap.put(389, "ldap");
		m_tcpPort2serviceMap.put(427, "svrloc");
		m_tcpPort2serviceMap.put(434, "mobileip-agent");
		m_tcpPort2serviceMap.put(435, "mobilip-mn");
		m_tcpPort2serviceMap.put(443, "https");
		m_tcpPort2serviceMap.put(444, "snpp");
		m_tcpPort2serviceMap.put(445, "microsoft-ds");
		m_tcpPort2serviceMap.put(464, "kpasswd");
		m_tcpPort2serviceMap.put(468, "photuris");
		m_tcpPort2serviceMap.put(487, "saft");
		m_tcpPort2serviceMap.put(488, "gss-http");
		m_tcpPort2serviceMap.put(496, "pim-rp-disc");
		m_tcpPort2serviceMap.put(500, "isakmp");
		m_tcpPort2serviceMap.put(538, "gdomap");
		m_tcpPort2serviceMap.put(535, "iiop");
		m_tcpPort2serviceMap.put(546, "dhcpv6-client");
		m_tcpPort2serviceMap.put(547, "dhcpv6-server");
		m_tcpPort2serviceMap.put(554, "rtsp");
		m_tcpPort2serviceMap.put(563, "nntps");
		m_tcpPort2serviceMap.put(565, "whoami");
		m_tcpPort2serviceMap.put(587, "submission");
		m_tcpPort2serviceMap.put(610, "npmp-local");
		m_tcpPort2serviceMap.put(611, "npmp-gui");
		m_tcpPort2serviceMap.put(612, "hmmp-ind");
		m_tcpPort2serviceMap.put(631, "ipp");
		m_tcpPort2serviceMap.put(636, "ldaps");
		m_tcpPort2serviceMap.put(674, "acap");
		m_tcpPort2serviceMap.put(694, "ha-cluster");
		m_tcpPort2serviceMap.put(749, "kerberos-adm");
		m_tcpPort2serviceMap.put(750, "kerberos-iv");
		m_tcpPort2serviceMap.put(765, "webster");
		m_tcpPort2serviceMap.put(767, "phonebook");
		m_tcpPort2serviceMap.put(873, "rsync");
		m_tcpPort2serviceMap.put(992, "telnets");
		m_tcpPort2serviceMap.put(993, "imaps");
		m_tcpPort2serviceMap.put(994, "ircs");
		m_tcpPort2serviceMap.put(995, "pop3s");
		m_tcpPort2serviceMap.put(512, "exec");
		m_tcpPort2serviceMap.put(513, "login");
		m_tcpPort2serviceMap.put(514, "shell");
		m_tcpPort2serviceMap.put(515, "printer");
		m_tcpPort2serviceMap.put(519, "utime");
		m_tcpPort2serviceMap.put(520, "efs");
		m_tcpPort2serviceMap.put(521, "ripng");
		m_tcpPort2serviceMap.put(525, "timed");
		m_tcpPort2serviceMap.put(526, "tempo");
		m_tcpPort2serviceMap.put(530, "courier");
		m_tcpPort2serviceMap.put(531, "conference");
		m_tcpPort2serviceMap.put(532, "netnews");
		m_tcpPort2serviceMap.put(540, "uucp");
		m_tcpPort2serviceMap.put(543, "klogin");
		m_tcpPort2serviceMap.put(544, "kshell");
		m_tcpPort2serviceMap.put(548, "afpovertcp");
		m_tcpPort2serviceMap.put(556, "remotefs");
		m_tcpPort2serviceMap.put(760, "krbupdate");
		m_tcpPort2serviceMap.put(871, "supfilesrv");
		m_tcpPort2serviceMap.put(15, "netstat");
		m_tcpPort2serviceMap.put(98, "linuxconf");
		m_tcpPort2serviceMap.put(106, "poppassd");
		m_tcpPort2serviceMap.put(465, "smtps");
		m_tcpPort2serviceMap.put(616, "gii");
		m_tcpPort2serviceMap.put(808, "omirr");
		m_tcpPort2serviceMap.put(901, "swat");
		m_tcpPort2serviceMap.put(953, "rndc");
		m_tcpPort2serviceMap.put(902, "vmware-authd");

		m_tcpPort2serviceMap.put(1080, "socks");
		m_tcpPort2serviceMap.put(1236, "bvcontrol");
		m_tcpPort2serviceMap.put(1300, "h323hostcallsc");
		m_tcpPort2serviceMap.put(1433, "ms-sql-s");
		m_tcpPort2serviceMap.put(1434, "ms-sql-m");
		m_tcpPort2serviceMap.put(1494, "ica");
		m_tcpPort2serviceMap.put(1512, "wins");
		m_tcpPort2serviceMap.put(1524, "ingreslock");
		m_tcpPort2serviceMap.put(1525, "prospero-np");
		m_tcpPort2serviceMap.put(1645, "datametrics");
		m_tcpPort2serviceMap.put(1646, "sa-msg-port");
		m_tcpPort2serviceMap.put(1649, "kermit");
		m_tcpPort2serviceMap.put(1701, "l2tp");
		m_tcpPort2serviceMap.put(1718, "h323gatedisc");
		m_tcpPort2serviceMap.put(1719, "h323gatestat");
		m_tcpPort2serviceMap.put(1720, "h323hostcall");
		m_tcpPort2serviceMap.put(1758, "tftp-mcast");
		m_tcpPort2serviceMap.put(1789, "hello");
		m_tcpPort2serviceMap.put(1812, "radius");
		m_tcpPort2serviceMap.put(1813, "radius-acct");
		m_tcpPort2serviceMap.put(1911, "mtp");
		m_tcpPort2serviceMap.put(1985, "hsrp");
		m_tcpPort2serviceMap.put(1986, "licensedaemon");
		m_tcpPort2serviceMap.put(1997, "gdp-port");
		m_tcpPort2serviceMap.put(2049, "nfs");
		m_tcpPort2serviceMap.put(2102, "zephyr-srv");
		m_tcpPort2serviceMap.put(2103, "zephyr-clt");
		m_tcpPort2serviceMap.put(2104, "zephyr-hm");
		m_tcpPort2serviceMap.put(2401, "cvspserver");
		m_tcpPort2serviceMap.put(2430, "venus");
		m_tcpPort2serviceMap.put(2431, "venus-se");
		m_tcpPort2serviceMap.put(2432, "codasrv");
		m_tcpPort2serviceMap.put(2433, "codasrv-se");
		m_tcpPort2serviceMap.put(2600, "hpstgmgr");
		m_tcpPort2serviceMap.put(2601, "discp-client");
		m_tcpPort2serviceMap.put(2602, "discp-server");
		m_tcpPort2serviceMap.put(2603, "servicemeter");
		m_tcpPort2serviceMap.put(2604, "nsc-ccs");
		m_tcpPort2serviceMap.put(2605, "nsc-posa");
		m_tcpPort2serviceMap.put(2606, "netmon");
		m_tcpPort2serviceMap.put(2809, "corbaloc");
		m_tcpPort2serviceMap.put(3130, "icpv2");
		m_tcpPort2serviceMap.put(3306, "mysql");
		m_tcpPort2serviceMap.put(3346, "trnsprntproxy");
		m_tcpPort2serviceMap.put(4321, "rwhois");
		m_tcpPort2serviceMap.put(4444, "krb524");
		m_tcpPort2serviceMap.put(5002, "rfe");
		m_tcpPort2serviceMap.put(5308, "cfengine");
		m_tcpPort2serviceMap.put(5666, "nrpe");
		m_tcpPort2serviceMap.put(5999, "cvsup");
		m_tcpPort2serviceMap.put(6000, "x11");
		m_tcpPort2serviceMap.put(7000, "afs3-fileserver");
		m_tcpPort2serviceMap.put(7001, "afs3-callback");
		m_tcpPort2serviceMap.put(7002, "afs3-prserver");
		m_tcpPort2serviceMap.put(7003, "afs3-vlserver");
		m_tcpPort2serviceMap.put(7004, "afs3-kaserver");
		m_tcpPort2serviceMap.put(7005, "afs3-volser");
		m_tcpPort2serviceMap.put(7006, "afs3-errors");
		m_tcpPort2serviceMap.put(7007, "afs3-bos");
		m_tcpPort2serviceMap.put(7008, "afs3-update");
		m_tcpPort2serviceMap.put(7009, "afs3-rmtsys");
		m_tcpPort2serviceMap.put(9876, "sd");
		m_tcpPort2serviceMap.put(10080, "amanda");
		m_tcpPort2serviceMap.put(11371, "pgpkeyserver");
		m_tcpPort2serviceMap.put(11720, "h323callsigalt");
		m_tcpPort2serviceMap.put(13720, "bprd");
		m_tcpPort2serviceMap.put(13721, "bpdbm");
		m_tcpPort2serviceMap.put(13722, "bpjava-msvc");
		m_tcpPort2serviceMap.put(13724, "vnetd");
		m_tcpPort2serviceMap.put(13782, "bpcd");
		m_tcpPort2serviceMap.put(13783, "vopied");
		m_tcpPort2serviceMap.put(22273, "wnn6");
		m_tcpPort2serviceMap.put(26000, "quake");
		m_tcpPort2serviceMap.put(26208, "wnn6-ds");
		m_tcpPort2serviceMap.put(33434, "traceroute");
		m_tcpPort2serviceMap.put(1109, "kpop");
		m_tcpPort2serviceMap.put(2053, "knetd");
		m_tcpPort2serviceMap.put(2105, "eklogin");
		m_tcpPort2serviceMap.put(1127, "supfiledbg");
		m_tcpPort2serviceMap.put(1178, "skkserv");
		m_tcpPort2serviceMap.put(1313, "xtel");
		m_tcpPort2serviceMap.put(1529, "support");
		m_tcpPort2serviceMap.put(2003, "cfinger");
		m_tcpPort2serviceMap.put(2150, "ninstall");
		m_tcpPort2serviceMap.put(2988, "afbackup");
		m_tcpPort2serviceMap.put(3128, "squid");
		m_tcpPort2serviceMap.put(3455, "prsvp");
		m_tcpPort2serviceMap.put(5432, "postgres");
		m_tcpPort2serviceMap.put(4557, "fax");
		m_tcpPort2serviceMap.put(4559, "hylafax");
		m_tcpPort2serviceMap.put(5232, "sgi-dgl");
		m_tcpPort2serviceMap.put(5354, "noclog");
		m_tcpPort2serviceMap.put(5355, "hostmon");
		m_tcpPort2serviceMap.put(5680, "canna");
		m_tcpPort2serviceMap.put(6010, "x11-ssh-offset");
		m_tcpPort2serviceMap.put(6667, "ircd");
		m_tcpPort2serviceMap.put(7100, "xfs");
		m_tcpPort2serviceMap.put(7666, "tircproxy");
		m_tcpPort2serviceMap.put(8008, "http-alt");
		m_tcpPort2serviceMap.put(8080, "webcache");
		m_tcpPort2serviceMap.put(8081, "tproxy");
		m_tcpPort2serviceMap.put(9100, "jetdirect");
		m_tcpPort2serviceMap.put(10081, "kamanda");
		m_tcpPort2serviceMap.put(10082, "amandaidx");
		m_tcpPort2serviceMap.put(10083, "amidxtape");
		m_tcpPort2serviceMap.put(20011, "isdnlog");
		m_tcpPort2serviceMap.put(20012, "vboxd");
		m_tcpPort2serviceMap.put(24554, "binkp");
		m_tcpPort2serviceMap.put(27374, "asp");
		m_tcpPort2serviceMap.put(60177, "tfido");
		m_tcpPort2serviceMap.put(60179, "fido");
		m_tcpPort2serviceMap.put(3632, "distcc");

		// personalized ports
		m_tcpPort2serviceMap.put(5666, "NRPE (Nagios)");
		m_tcpPort2serviceMap.put(6882, "Bittorrent");
	}

	private Port2Services()
	{

	}

	public static Port2Services getInstance()
	{

		if (m_instance == null)
			m_instance = new Port2Services();

		return m_instance;
	}

	public String getService(Integer portNumber, Protocol protocol)
	{
		String service = null;

		if (protocol != null)
		{
			switch (protocol)
			{
				case tcp:
					service = m_tcpPort2serviceMap.get(portNumber);
					break;
				case udp:
					service = m_udpPort2serviceMap.get(portNumber);
					break;
				case icmp:
					// ICMP protocol do not use ports, it is in the IP level and
					// consequently it haven got place for the port.
					break;
			}
		}

		if (service != null)
			return service;
		return "";
	}

}
