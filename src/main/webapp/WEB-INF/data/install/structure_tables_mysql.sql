/*!40101 SET NAMES utf8 */;

#
# Structure for table "comment"
#

CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `quote` longtext,
  `quoteIdGroup` varchar(255) DEFAULT NULL,
  `quoteUpdateId` varchar(255) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comment_1_idx` (`topicId`,`status`),
  KEY `comment_2_idx` (`quoteIdGroup`(191)),
  KEY `comment_3_idx` (`userName`,`isStaff`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "customreply"
#

CREATE TABLE `customreply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `customCommentId` bigint(20) DEFAULT NULL,
  `customItemId` bigint(20) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `staff` bit(1) NOT NULL,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customReply_1_idx` (`customCommentId`),
  KEY `customReply_2_idx` (`customItemId`),
  KEY `customReply_3_idx` (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "disableusername"
#

CREATE TABLE `disableusername` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "favorites_0"
#

CREATE TABLE `favorites_0` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `favorites_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "favorites_1"
#

CREATE TABLE `favorites_1` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `favorites_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "favorites_2"
#

CREATE TABLE `favorites_2` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `favorites_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "favorites_3"
#

CREATE TABLE `favorites_3` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `favorites_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "feedback"
#

CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contact` varchar(190) DEFAULT NULL,
  `content` longtext,
  `createDate` datetime DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `name` varchar(190) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `feedback_1_idx` (`createDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follow_0"
#

CREATE TABLE `follow_0` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follow_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follow_1"
#

CREATE TABLE `follow_1` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follow_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follow_2"
#

CREATE TABLE `follow_2` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follow_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follow_3"
#

CREATE TABLE `follow_3` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follow_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follower_0"
#

CREATE TABLE `follower_0` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follower_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follower_1"
#

CREATE TABLE `follower_1` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follower_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follower_2"
#

CREATE TABLE `follower_2` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follower_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "follower_3"
#

CREATE TABLE `follower_3` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `friendUserName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `follower_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "forum"
#

CREATE TABLE `forum` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dirName` varchar(40) DEFAULT NULL,
  `displayType` varchar(10) DEFAULT NULL,
  `formValue` longtext,
  `forumChildType` varchar(40) DEFAULT NULL,
  `forumType` varchar(40) DEFAULT NULL,
  `invokeMethod` int(11) DEFAULT NULL,
  `layoutFile` varchar(40) DEFAULT NULL,
  `layoutId` varchar(36) DEFAULT NULL,
  `layoutType` int(11) DEFAULT NULL,
  `module` varchar(80) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `queryMode` int(11) DEFAULT NULL,
  `referenceCode` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "help"
#

CREATE TABLE `help` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `helpTypeId` bigint(20) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `visible` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `help_idx` (`helpTypeId`,`visible`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "helptype"
#

CREATE TABLE `helptype` (
  `id` bigint(20) NOT NULL,
  `childNodeNumber` int(11) DEFAULT NULL,
  `helpQuantity` bigint(20) DEFAULT NULL,
  `mergerTypeId` varchar(1000) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `parentIdGroup` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `totalHelp` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `helpType_1_idx` (`parentId`),
  KEY `helpType_2_idx` (`parentIdGroup`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "layout"
#

CREATE TABLE `layout` (
  `id` varchar(36) NOT NULL,
  `dirName` varchar(40) DEFAULT NULL,
  `forumData` int(11) DEFAULT NULL,
  `layoutFile` varchar(40) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `referenceCode` varchar(40) DEFAULT NULL,
  `returnData` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "like_0"
#

CREATE TABLE `like_0` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `like_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "like_1"
#

CREATE TABLE `like_1` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `like_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "like_2"
#

CREATE TABLE `like_2` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `like_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "like_3"
#

CREATE TABLE `like_3` (
  `id` varchar(36) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `like_1_idx` (`userName`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "links"
#

CREATE TABLE `links` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createDate` datetime DEFAULT NULL,
  `image` varchar(100) DEFAULT NULL,
  `name` varchar(190) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `website` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "pointlog_0"
#

CREATE TABLE `pointlog_0` (
  `id` varchar(36) NOT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `parameterId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `pointState` int(11) NOT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pointlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "pointlog_1"
#

CREATE TABLE `pointlog_1` (
  `id` varchar(36) NOT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `parameterId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `pointState` int(11) NOT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pointlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "pointlog_2"
#

CREATE TABLE `pointlog_2` (
  `id` varchar(36) NOT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `parameterId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `pointState` int(11) NOT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pointlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "pointlog_3"
#

CREATE TABLE `pointlog_3` (
  `id` varchar(36) NOT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `parameterId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `pointState` int(11) NOT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pointlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `privatemessage_0` (
  `id` varchar(36) NOT NULL,
  `friendUserId` bigint(20) DEFAULT NULL,
  `messageContent` longtext,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `privateMessage_1_idx` (`userId`,`status`,`sendTimeFormat`),
  KEY `privateMessage_2_idx` (`userId`,`friendUserId`,`status`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "privatemessage_1"
#

CREATE TABLE `privatemessage_1` (
  `id` varchar(36) NOT NULL,
  `friendUserId` bigint(20) DEFAULT NULL,
  `messageContent` longtext,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `privateMessage_1_idx` (`userId`,`status`,`sendTimeFormat`),
  KEY `privateMessage_2_idx` (`userId`,`friendUserId`,`status`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "privatemessage_2"
#

CREATE TABLE `privatemessage_2` (
  `id` varchar(36) NOT NULL,
  `friendUserId` bigint(20) DEFAULT NULL,
  `messageContent` longtext,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `privateMessage_1_idx` (`userId`,`status`,`sendTimeFormat`),
  KEY `privateMessage_2_idx` (`userId`,`friendUserId`,`status`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "privatemessage_3"
#

CREATE TABLE `privatemessage_3` (
  `id` varchar(36) NOT NULL,
  `friendUserId` bigint(20) DEFAULT NULL,
  `messageContent` longtext,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `privateMessage_1_idx` (`userId`,`status`,`sendTimeFormat`),
  KEY `privateMessage_2_idx` (`userId`,`friendUserId`,`status`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "pv"
#

CREATE TABLE `pv` (
  `id` varchar(32) NOT NULL,
  `browserName` varchar(255) DEFAULT NULL,
  `deviceType` varchar(255) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `operatingSystem` varchar(255) DEFAULT NULL,
  `referrer` longtext,
  `times` datetime DEFAULT NULL,
  `url` longtext,
  PRIMARY KEY (`id`),
  KEY `pv_1_idx` (`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "remind_0"
#

CREATE TABLE `remind_0` (
  `id` varchar(36) NOT NULL,
  `friendTopicCommentId` bigint(20) DEFAULT NULL,
  `friendTopicReplyId` bigint(20) DEFAULT NULL,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicCommentId` bigint(20) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `topicReplyId` bigint(20) DEFAULT NULL,
  `typeCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "remind_1"
#

CREATE TABLE `remind_1` (
  `id` varchar(36) NOT NULL,
  `friendTopicCommentId` bigint(20) DEFAULT NULL,
  `friendTopicReplyId` bigint(20) DEFAULT NULL,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicCommentId` bigint(20) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `topicReplyId` bigint(20) DEFAULT NULL,
  `typeCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "remind_2"
#

CREATE TABLE `remind_2` (
  `id` varchar(36) NOT NULL,
  `friendTopicCommentId` bigint(20) DEFAULT NULL,
  `friendTopicReplyId` bigint(20) DEFAULT NULL,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicCommentId` bigint(20) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `topicReplyId` bigint(20) DEFAULT NULL,
  `typeCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "remind_3"
#

CREATE TABLE `remind_3` (
  `id` varchar(36) NOT NULL,
  `friendTopicCommentId` bigint(20) DEFAULT NULL,
  `friendTopicReplyId` bigint(20) DEFAULT NULL,
  `readTimeFormat` bigint(20) DEFAULT NULL,
  `receiverUserId` bigint(20) DEFAULT NULL,
  `sendTimeFormat` bigint(20) DEFAULT NULL,
  `senderUserId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicCommentId` bigint(20) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `topicReplyId` bigint(20) DEFAULT NULL,
  `typeCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "reply"
#

CREATE TABLE `reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `commentId` bigint(20) DEFAULT NULL,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reply_1_idx` (`commentId`,`status`),
  KEY `reply_2_idx` (`topicId`),
  KEY `reply_3_idx` (`userName`,`isStaff`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "sendsmslog"
#

CREATE TABLE `sendsmslog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(60) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `message` varchar(200) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `serviceId` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "smsinterface"
#

CREATE TABLE `smsinterface` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dynamicParameter` longtext,
  `enable` bit(1) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `sendService` longtext,
  `sort` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "staffloginlog_0"
#

CREATE TABLE `staffloginlog_0` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `staffId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `staffLoginLog_idx` (`staffId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "staffloginlog_1"
#

CREATE TABLE `staffloginlog_1` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `staffId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `staffLoginLog_idx` (`staffId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "staffloginlog_2"
#

CREATE TABLE `staffloginlog_2` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `staffId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `staffLoginLog_idx` (`staffId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "staffloginlog_3"
#

CREATE TABLE `staffloginlog_3` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `staffId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `staffLoginLog_idx` (`staffId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "subscriptionsystemnotify_0"
#

CREATE TABLE `subscriptionsystemnotify_0` (
  `id` varchar(36) NOT NULL,
  `readTime` datetime DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `systemNotifyId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptionSystemNotify_1_idx` (`systemNotifyId`),
  KEY `subscriptionSystemNotify_2_idx` (`userId`,`status`,`systemNotifyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "subscriptionsystemnotify_1"
#

CREATE TABLE `subscriptionsystemnotify_1` (
  `id` varchar(36) NOT NULL,
  `readTime` datetime DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `systemNotifyId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptionSystemNotify_1_idx` (`systemNotifyId`),
  KEY `subscriptionSystemNotify_2_idx` (`userId`,`status`,`systemNotifyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "subscriptionsystemnotify_2"
#

CREATE TABLE `subscriptionsystemnotify_2` (
  `id` varchar(36) NOT NULL,
  `readTime` datetime DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `systemNotifyId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptionSystemNotify_1_idx` (`systemNotifyId`),
  KEY `subscriptionSystemNotify_2_idx` (`userId`,`status`,`systemNotifyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "subscriptionsystemnotify_3"
#

CREATE TABLE `subscriptionsystemnotify_3` (
  `id` varchar(36) NOT NULL,
  `readTime` datetime DEFAULT NULL,
  `sendTime` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `systemNotifyId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptionSystemNotify_1_idx` (`systemNotifyId`),
  KEY `subscriptionSystemNotify_2_idx` (`userId`,`status`,`systemNotifyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



#
# Structure for table "syspermission"
#

CREATE TABLE `syspermission` (
  `id` varchar(32) NOT NULL,
  `methods` varchar(4) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "syspermissionresources"
#

CREATE TABLE `syspermissionresources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permissionId` varchar(255) DEFAULT NULL,
  `resourceId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "sysresources"
#

CREATE TABLE `sysresources` (
  `id` varchar(32) NOT NULL,
  `module` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `urlParentId` varchar(255) DEFAULT NULL,
  `urlType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "sysroles"
#

CREATE TABLE `sysroles` (
  `id` varchar(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "sysrolespermission"
#

CREATE TABLE `sysrolespermission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permissionId` varchar(255) DEFAULT NULL,
  `roleId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "systemnotify"
#

CREATE TABLE `systemnotify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `sendTime` datetime DEFAULT NULL,
  `staffName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `systemNotify_1_idx` (`sendTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "systemsetting"
#


CREATE TABLE `systemsetting` (
  `id` int(11) NOT NULL,
  `allowRegister` bit(1) NOT NULL,
  `backstagePageNumber` int(11) DEFAULT NULL,
  `closeSite` int(11) DEFAULT NULL,
  `closeSitePrompt` longtext,
  `description` varchar(255) DEFAULT NULL,
  `editorTag` longtext,
  `forestagePageNumber` int(11) DEFAULT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `login_submitQuantity` int(11) DEFAULT NULL,
  `registerCaptcha` bit(1) NOT NULL,
  `supportAccessDevice` int(11) DEFAULT NULL,
  `temporaryFileValidPeriod` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `userSentSmsCount` int(11) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `comment_submitQuantity` int(11) DEFAULT NULL,
  `topic_submitQuantity` int(11) DEFAULT NULL,
  `realNameUserAllowComment` bit(1) NOT NULL,
  `realNameUserAllowTopic` bit(1) NOT NULL,
  `comment_rewardPoint` bigint(20) DEFAULT NULL,
  `reply_rewardPoint` bigint(20) DEFAULT NULL,
  `topic_rewardPoint` bigint(20) DEFAULT NULL,
  `allowTopic` bit(1) NOT NULL,
  `allowFeedback` bit(1) NOT NULL,
  `allowComment` bit(1) NOT NULL,
  `allowFilterWord` bit(1) NOT NULL,
  `filterWordReplace` varchar(255) DEFAULT NULL,
  `privateMessage_submitQuantity` int(11) DEFAULT NULL,
  `topicEditorTag` longtext,
  `comment_review` int(11) DEFAULT NULL,
  `reply_review` int(11) DEFAULT NULL,
  `topic_review` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "sysusers"
#

CREATE TABLE `sysusers` (
  `userId` varchar(32) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `issys` bit(1) NOT NULL,
  `userDesc` varchar(255) DEFAULT NULL,
  `userDuty` varchar(255) DEFAULT NULL,
  `userPassword` varchar(255) DEFAULT NULL,
  `userAccount` varchar(30) DEFAULT NULL,
  `securityDigest` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UKi13uxat3wa9pt2glvqr05g7rn` (`userAccount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;





#
# Structure for table "sysusersroles"
#

CREATE TABLE `sysusersroles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleId` varchar(255) DEFAULT NULL,
  `userAccount` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "tag"
#

CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL,
  `name` varchar(190) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_1_idx` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "templates"
#

CREATE TABLE `templates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `columns` longtext,
  `dirName` varchar(40) DEFAULT NULL,
  `introduction` longtext,
  `name` varchar(255) DEFAULT NULL,
  `thumbnailSuffix` varchar(20) DEFAULT NULL,
  `uses` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "thumbnail"
#

CREATE TABLE `thumbnail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `high` int(11) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `specificationGroup` varchar(25) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi1c51f71y0grwc0d7wyoe5mig` (`specificationGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "timetask"
#

CREATE TABLE `timetask` (
  `id` varchar(191) NOT NULL,
  `cron` varchar(250) DEFAULT NULL,
  `misfireInstruction` int(11) DEFAULT NULL,
  `performType` varchar(20) DEFAULT NULL,
  `remark` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topic"
#

CREATE TABLE `topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allow` bit(1) NOT NULL,
  `commentTotal` bigint(20) DEFAULT NULL,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `lastReplyTime` datetime DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `tagId` bigint(20) DEFAULT NULL,
  `title` varchar(190) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `viewTotal` bigint(20) DEFAULT NULL,
  `image` longtext,
  `status` int(11) DEFAULT NULL,
  `summary` longtext,
  `sort` int(11) DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topic_idx` (`tagId`,`status`),
  KEY `topic_3_idx` (`userName`,`postTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#

#
# Structure for table "topicfavorite_0"
#

CREATE TABLE `topicfavorite_0` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicFavorite_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicfavorite_1"
#

CREATE TABLE `topicfavorite_1` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicFavorite_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicfavorite_2"
#

CREATE TABLE `topicfavorite_2` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicFavorite_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicfavorite_3"
#

CREATE TABLE `topicfavorite_3` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicFavorite_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicindex"
#

CREATE TABLE `topicindex` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataId` varchar(32) DEFAULT NULL,
  `indexState` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topiclike_0"
#

CREATE TABLE `topiclike_0` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicLike_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topiclike_1"
#

CREATE TABLE `topiclike_1` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicLike_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topiclike_2"
#

CREATE TABLE `topiclike_2` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicLike_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topiclike_3"
#

CREATE TABLE `topiclike_3` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicLike_1_idx` (`topicId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "topicunhide_0"
#

CREATE TABLE `topicunhide_0` (
  `id` varchar(43) NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `cancelTime` datetime DEFAULT NULL,
  `hideTagType` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicUnhide_1_idx` (`topicId`,`cancelTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicunhide_1"
#

CREATE TABLE `topicunhide_1` (
  `id` varchar(43) NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `cancelTime` datetime DEFAULT NULL,
  `hideTagType` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicunhide_1_idx` (`topicId`,`cancelTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicunhide_2"
#

CREATE TABLE `topicunhide_2` (
  `id` varchar(43) NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `cancelTime` datetime DEFAULT NULL,
  `hideTagType` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicunhide_1_idx` (`topicId`,`cancelTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "topicunhide_3"
#

CREATE TABLE `topicunhide_3` (
  `id` varchar(43) NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `cancelTime` datetime DEFAULT NULL,
  `hideTagType` int(11) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicunhide_1_idx` (`topicId`,`cancelTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "upgradesystem"
#

CREATE TABLE `upgradesystem` (
  `id` varchar(32) NOT NULL,
  `deleteFilePath` longtext,
  `explanation` longtext,
  `interruptStatus` int(11) DEFAULT NULL,
  `oldSystemVersion` varchar(100) DEFAULT NULL,
  `runningStatus` int(11) DEFAULT NULL,
  `sort` bigint(20) DEFAULT NULL,
  `updatePackageFirstDirectory` varchar(100) DEFAULT NULL,
  `updatePackageName` varchar(100) DEFAULT NULL,
  `updatePackageTime` datetime DEFAULT NULL,
  `updatePackageVersion` varchar(100) DEFAULT NULL,
  `upgradeLog` longtext,
  `upgradeTime` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "user"
#

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `answer` varchar(80) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `issue` varchar(50) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `password` varchar(160) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `realNameAuthentication` bit(1) NOT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `remarks` longtext,
  `salt` varchar(80) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `userVersion` int(11) DEFAULT NULL,
  `securityDigest` bigint(20) DEFAULT NULL,
  `avatarName` varchar(50) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `allowUserDynamic` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4bakctviobmdk6ddh2nwg08c2` (`userName`),
  KEY `user_idx` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



#
# Structure for table "usercustom"
#

CREATE TABLE `usercustom` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chooseType` int(11) DEFAULT NULL,
  `cols` int(11) DEFAULT NULL,
  `fieldFilter` int(11) DEFAULT NULL,
  `maxlength` int(11) DEFAULT NULL,
  `multiple` bit(1) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `regular` longtext,
  `required` bit(1) NOT NULL,
  `rows` int(11) DEFAULT NULL,
  `search` bit(1) NOT NULL,
  `selete_size` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `tip` varchar(250) DEFAULT NULL,
  `value` longtext,
  `visible` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



#
# Structure for table "userdynamic_0"
#

CREATE TABLE `userdynamic_0` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_2_idx` (`topicId`,`userName`,`module`),
  KEY `userDynamic_3_idx` (`commentId`,`userName`,`module`),
  KEY `userDynamic_4_idx` (`replyId`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_1"
#

CREATE TABLE `userdynamic_1` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_2_idx` (`topicId`,`userName`,`module`),
  KEY `userDynamic_3_idx` (`commentId`,`userName`,`module`),
  KEY `userDynamic_4_idx` (`replyId`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_2"
#

CREATE TABLE `userdynamic_2` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_2_idx` (`topicId`,`userName`,`module`),
  KEY `userDynamic_3_idx` (`commentId`,`userName`,`module`),
  KEY `userDynamic_4_idx` (`replyId`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_3"
#

CREATE TABLE `userdynamic_3` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_2_idx` (`topicId`,`userName`,`module`),
  KEY `userDynamic_3_idx` (`commentId`,`userName`,`module`),
  KEY `userDynamic_4_idx` (`replyId`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "usergrade"
#

CREATE TABLE `usergrade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `needPoint` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userinputvalue"
#

CREATE TABLE `userinputvalue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `options` varchar(32) DEFAULT NULL,
  `userCustomId` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userinputvalue_idx` (`userId`,`options`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userloginlog_0"
#

CREATE TABLE `userloginlog_0` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `typeNumber` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userLoginLog_idx` (`userId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userloginlog_1"
#

CREATE TABLE `userloginlog_1` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `typeNumber` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userLoginLog_idx` (`userId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userloginlog_2"
#

CREATE TABLE `userloginlog_2` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `typeNumber` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userLoginLog_idx` (`userId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userloginlog_3"
#

CREATE TABLE `userloginlog_3` (
  `id` varchar(36) NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `logonTime` datetime DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `typeNumber` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userLoginLog_idx` (`userId`,`logonTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userrole"
#

CREATE TABLE `userrole` (
  `id` varchar(32) NOT NULL,
  `defaultRole` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `remark` longtext,
  `sort` int(11) DEFAULT NULL,
  `userResourceFormat` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userrolegroup"
#

CREATE TABLE `userrolegroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userName` varchar(30) DEFAULT NULL,
  `userRoleId` varchar(32) DEFAULT NULL,
  `validPeriodEnd` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userRoleGroup_1_idx` (`userName`,`validPeriodEnd`),
  KEY `userRoleGroup_2_idx` (`userRoleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;