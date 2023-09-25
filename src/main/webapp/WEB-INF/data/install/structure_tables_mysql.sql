/*!40101 SET NAMES utf8 */;

#
# Structure for table "answer"
#

CREATE TABLE `answer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `adoption` bit(1) DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `isMarkdown` bit(1) DEFAULT NULL,
  `markdownContent` longtext,
  PRIMARY KEY (`id`),
  KEY `answer_1_idx` (`questionId`,`status`,`adoption`),
  KEY `answer_2_idx` (`userName`,`isStaff`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "answerreply"
#

CREATE TABLE `answerreply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `answerId` bigint(20) DEFAULT NULL,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `friendReplyId` bigint(20) DEFAULT NULL,
  `friendReplyIdGroup` longtext,
  `friendUserName` varchar(30) DEFAULT NULL,
  `isFriendStaff` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `answerReply_1_idx` (`answerId`,`status`),
  KEY `answerReply_2_idx` (`questionId`),
  KEY `answerReply_3_idx` (`userName`,`isStaff`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `lastUpdateTime` datetime DEFAULT NULL,
  `isMarkdown` bit(1) DEFAULT NULL,
  `markdownContent` longtext,
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
  `module` int(11) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
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
  `module` int(11) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
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
  `module` int(11) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
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
  `module` int(11) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
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
# Structure for table "giveredenvelope"
#

CREATE TABLE `giveredenvelope` (
  `id` varchar(32) NOT NULL,
  `bindTopicId` bigint(20) DEFAULT NULL,
  `distributionAmountGroup` longtext,
  `giveQuantity` int(11) DEFAULT NULL,
  `giveTime` datetime DEFAULT NULL,
  `grabRedEnvelopeUserIdGroup` longtext,
  `remainingQuantity` int(11) DEFAULT NULL,
  `singleAmount` decimal(12,2) DEFAULT NULL,
  `totalAmount` decimal(12,2) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `wishes` varchar(150) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `refundAmount` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `giveRedEnvelope_1_idx` (`bindTopicId`),
  KEY `giveRedEnvelope_2_idx` (`userId`,`giveTime`)
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
  `isMarkdown` bit(1) DEFAULT NULL,
  `markdownContent` longtext,
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
  `description` longtext,
  `image` varchar(100) DEFAULT NULL,
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
  `referenceCode` varchar(100) DEFAULT NULL,
  `returnData` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `accessRequireLogin` bit(1) NOT NULL,
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
# Structure for table "mediaprocessqueue"
#

CREATE TABLE `mediaprocessqueue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filePath` varchar(255) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `fileName` varchar(100) DEFAULT NULL,
  `parameter` varchar(100) DEFAULT NULL,
  `processProgress` double DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `errorInfo` longtext,
  `ip` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mediaProcessQueue_2_idx` (`fileName`),
  KEY `mediaProcessQueue_1_idx` (`processProgress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "membershipcard"
#

CREATE TABLE `membershipcard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createDate` datetime DEFAULT NULL,
  `descriptionTagFormat` longtext,
  `highestPoint` bigint(20) DEFAULT NULL,
  `highestPrice` decimal(12,2) DEFAULT NULL,
  `introduction` longtext,
  `lowestPoint` bigint(20) DEFAULT NULL,
  `lowestPrice` decimal(12,2) DEFAULT NULL,
  `name` varchar(190) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `subtitle` varchar(190) DEFAULT NULL,
  `userRoleId` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `membershipCard_1_idx` (`createDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "membershipcardgiftitem_0"
#

CREATE TABLE `membershipcardgiftitem_0` (
  `id` varchar(65) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `membershipCardGiftTaskId` bigint(20) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `restriction` longtext,
  PRIMARY KEY (`id`),
  KEY `membershipCardGiftItem_idx` (`membershipCardGiftTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "membershipcardgiftitem_1"
#

CREATE TABLE `membershipcardgiftitem_1` (
  `id` varchar(65) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `membershipCardGiftTaskId` bigint(20) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `restriction` longtext,
  PRIMARY KEY (`id`),
  KEY `membershipCardGiftItem_idx` (`membershipCardGiftTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "membershipcardgiftitem_2"
#

CREATE TABLE `membershipcardgiftitem_2` (
  `id` varchar(65) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `membershipCardGiftTaskId` bigint(20) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `restriction` longtext,
  PRIMARY KEY (`id`),
  KEY `membershipCardGiftItem_idx` (`membershipCardGiftTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "membershipcardgiftitem_3"
#

CREATE TABLE `membershipcardgiftitem_3` (
  `id` varchar(65) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `membershipCardGiftTaskId` bigint(20) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `restriction` longtext,
  PRIMARY KEY (`id`),
  KEY `membershipCardGiftItem_idx` (`membershipCardGiftTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "membershipcardgifttask"
#

CREATE TABLE `membershipcardgifttask` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createDate` datetime DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `enable` bit(1) NOT NULL,
  `name` varchar(190) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `expirationDate_end` datetime DEFAULT NULL,
  `expirationDate_start` datetime DEFAULT NULL,
  `restriction` longtext,
  `version` int(11) DEFAULT NULL,
  `userRoleId` varchar(32) DEFAULT NULL,
  `userRoleName` varchar(192) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `membershipCardGiftTask_1_idx` (`expirationDate_start`,`expirationDate_end`,`type`,`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "membershipcardorder"
#

CREATE TABLE `membershipcardorder` (
  `orderId` bigint(20) NOT NULL,
  `accountPayable` decimal(12,2) NOT NULL,
  `accountPoint` bigint(20) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `membershipCardId` bigint(20) DEFAULT NULL,
  `paymentAmount` decimal(12,2) NOT NULL,
  `paymentPoint` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `roleName` varchar(192) DEFAULT NULL,
  `specificationId` bigint(20) DEFAULT NULL,
  `specificationName` varchar(192) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `userRoleId` varchar(32) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`orderId`),
  KEY `membershipCardOrder_1_idx` (`userName`,`createDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "onlinepaymentinterface"
#

CREATE TABLE `onlinepaymentinterface` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dynamicParameter` longtext,
  `enable` bit(1) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `supportEquipment` varchar(5) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "paymentlog_0"
#

CREATE TABLE `paymentlog_0` (
  `paymentRunningNumber` varchar(32) NOT NULL,
  `amount` decimal(14,4) NOT NULL,
  `amountState` int(11) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `paymentModule` int(11) DEFAULT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `tradeNo` varchar(255) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `sourceParameterId` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`paymentRunningNumber`),
  KEY `paymentlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "paymentlog_1"
#

CREATE TABLE `paymentlog_1` (
  `paymentRunningNumber` varchar(32) NOT NULL,
  `amount` decimal(14,4) NOT NULL,
  `amountState` int(11) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `paymentModule` int(11) DEFAULT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `tradeNo` varchar(255) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `sourceParameterId` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`paymentRunningNumber`),
  KEY `paymentlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "paymentlog_2"
#

CREATE TABLE `paymentlog_2` (
  `paymentRunningNumber` varchar(32) NOT NULL,
  `amount` decimal(14,4) NOT NULL,
  `amountState` int(11) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `paymentModule` int(11) DEFAULT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `tradeNo` varchar(255) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `sourceParameterId` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`paymentRunningNumber`),
  KEY `paymentlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "paymentlog_3"
#

CREATE TABLE `paymentlog_3` (
  `paymentRunningNumber` varchar(32) NOT NULL,
  `amount` decimal(14,4) NOT NULL,
  `amountState` int(11) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `operationUserName` varchar(50) DEFAULT NULL,
  `operationUserType` int(11) DEFAULT NULL,
  `paymentModule` int(11) DEFAULT NULL,
  `remark` longtext,
  `times` datetime DEFAULT NULL,
  `tradeNo` varchar(255) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `sourceParameterId` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`paymentRunningNumber`),
  KEY `paymentlog_idx` (`userName`,`times`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#
# Structure for table "paymentverificationlog"
#

CREATE TABLE `paymentverificationlog` (
  `id` varchar(32) NOT NULL,
  `parameterId` bigint(20) DEFAULT NULL,
  `paymentAmount` decimal(12,2) NOT NULL,
  `paymentModule` int(11) DEFAULT NULL,
  `times` datetime DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userName_idx` (`parameterId`,`userName`)
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
# Structure for table "question"
#

CREATE TABLE `question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allow` bit(1) NOT NULL,
  `answerTotal` bigint(20) DEFAULT NULL,
  `appendContent` longtext,
  `content` longtext,
  `ip` varchar(45) DEFAULT NULL,
  `isStaff` bit(1) DEFAULT NULL,
  `lastAnswerTime` datetime DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `summary` longtext,
  `title` varchar(190) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `viewTotal` bigint(20) DEFAULT NULL,
  `adoptionAnswerId` bigint(20) DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `point` bigint(20) DEFAULT NULL,
  `isMarkdown` bit(1) DEFAULT NULL,
  `markdownContent` longtext,
  PRIMARY KEY (`id`),
  KEY `question_1_idx` (`userName`,`postTime`),
  KEY `question_4_idx` (`status`,`sort`,`lastAnswerTime`),
  KEY `question_5_idx` (`adoptionAnswerId`,`status`,`sort`,`lastAnswerTime`),
  KEY `question_6_idx` (`point`,`status`,`sort`,`lastAnswerTime`),
  KEY `question_7_idx` (`amount`,`status`,`sort`,`lastAnswerTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionfavorite_0"
#

CREATE TABLE `questionfavorite_0` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionFavorite_1_idx` (`questionId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionfavorite_1"
#

CREATE TABLE `questionfavorite_1` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionFavorite_1_idx` (`questionId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionfavorite_2"
#

CREATE TABLE `questionfavorite_2` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionFavorite_1_idx` (`questionId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionfavorite_3"
#

CREATE TABLE `questionfavorite_3` (
  `id` varchar(40) NOT NULL,
  `addtime` datetime DEFAULT NULL,
  `postUserName` varchar(30) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionFavorite_1_idx` (`questionId`,`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionindex"
#

CREATE TABLE `questionindex` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataId` varchar(32) DEFAULT NULL,
  `indexState` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questionrewardplatformshare"
#

CREATE TABLE `questionrewardplatformshare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `adoptionTime` datetime DEFAULT NULL,
  `answerUserName` varchar(30) DEFAULT NULL,
  `answerUserShareRunningNumber` varchar(32) DEFAULT NULL,
  `platformShareProportion` int(11) DEFAULT NULL,
  `postUserName` varchar(80) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `shareAmount` decimal(14,4) NOT NULL,
  `staff` bit(1) NOT NULL,
  `totalAmount` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `questionRewardPlatformShare_1_idx` (`adoptionTime`),
  KEY `questionRewardPlatformShare_2_idx` (`questionId`,`answerUserName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questiontag"
#

CREATE TABLE `questiontag` (
  `id` bigint(20) NOT NULL,
  `childNodeNumber` int(11) DEFAULT NULL,
  `name` varchar(190) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `parentIdGroup` varchar(190) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionTag_1_idx` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "questiontagassociation"
#

CREATE TABLE `questiontagassociation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `questionId` bigint(20) DEFAULT NULL,
  `questionTagId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionTagAssociation_1_idx` (`questionId`),
  KEY `questionTagAssociation_2_idx` (`questionTagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "receiveredenvelope_0"
#

CREATE TABLE `receiveredenvelope_0` (
  `id` varchar(80) NOT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `giveRedEnvelopeId` varchar(32) DEFAULT NULL,
  `giveUserId` bigint(20) DEFAULT NULL,
  `receiveTime` datetime DEFAULT NULL,
  `receiveUserId` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiveRedEnvelope_1_idx` (`receiveUserId`,`receiveTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "receiveredenvelope_1"
#

CREATE TABLE `receiveredenvelope_1` (
  `id` varchar(80) NOT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `giveRedEnvelopeId` varchar(32) DEFAULT NULL,
  `giveUserId` bigint(20) DEFAULT NULL,
  `receiveTime` datetime DEFAULT NULL,
  `receiveUserId` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiveRedEnvelope_1_idx` (`receiveUserId`,`receiveTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "receiveredenvelope_2"
#

CREATE TABLE `receiveredenvelope_2` (
  `id` varchar(80) NOT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `giveRedEnvelopeId` varchar(32) DEFAULT NULL,
  `giveUserId` bigint(20) DEFAULT NULL,
  `receiveTime` datetime DEFAULT NULL,
  `receiveUserId` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiveRedEnvelope_1_idx` (`receiveUserId`,`receiveTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "receiveredenvelope_3"
#

CREATE TABLE `receiveredenvelope_3` (
  `id` varchar(80) NOT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `giveRedEnvelopeId` varchar(32) DEFAULT NULL,
  `giveUserId` bigint(20) DEFAULT NULL,
  `receiveTime` datetime DEFAULT NULL,
  `receiveUserId` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiveRedEnvelope_1_idx` (`receiveUserId`,`receiveTime`)
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
  `friendQuestionAnswerId` bigint(20) DEFAULT NULL,
  `friendQuestionReplyId` bigint(20) DEFAULT NULL,
  `questionAnswerId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `questionReplyId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`),
  KEY `remind_4_idx` (`questionId`)
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
  `friendQuestionAnswerId` bigint(20) DEFAULT NULL,
  `friendQuestionReplyId` bigint(20) DEFAULT NULL,
  `questionAnswerId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `questionReplyId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`),
  KEY `remind_4_idx` (`questionId`)
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
  `friendQuestionAnswerId` bigint(20) DEFAULT NULL,
  `friendQuestionReplyId` bigint(20) DEFAULT NULL,
  `questionAnswerId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `questionReplyId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`),
  KEY `remind_4_idx` (`questionId`)
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
  `friendQuestionAnswerId` bigint(20) DEFAULT NULL,
  `friendQuestionReplyId` bigint(20) DEFAULT NULL,
  `questionAnswerId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `questionReplyId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `remind_1_idx` (`receiverUserId`,`status`,`sendTimeFormat`),
  KEY `remind_2_idx` (`topicId`),
  KEY `remind_3_idx` (`receiverUserId`,`typeCode`,`sendTimeFormat`),
  KEY `remind_4_idx` (`questionId`)
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
  `lastUpdateTime` datetime DEFAULT NULL,
  `friendReplyId` bigint(20) DEFAULT NULL,
  `friendReplyIdGroup` longtext,
  `friendUserName` varchar(30) DEFAULT NULL,
  `isFriendStaff` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reply_1_idx` (`commentId`,`status`),
  KEY `reply_2_idx` (`topicId`),
  KEY `reply_3_idx` (`userName`,`isStaff`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "report"
#

CREATE TABLE `report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `imageData` longtext,
  `postTime` datetime DEFAULT NULL,
  `processCompleteTime` datetime DEFAULT NULL,
  `processResult` longtext,
  `reason` longtext,
  `remark` longtext,
  `reportTypeId` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `parameterId` varchar(65) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `extraParameterId` varchar(130) DEFAULT NULL,
  `staffAccount` varchar(30) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_1_idx` (`parameterId`,`module`,`status`),
  KEY `report_2_idx` (`userName`,`status`),
  KEY `report_3_idx` (`parameterId`,`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "reporttype"
#

CREATE TABLE `reporttype` (
  `id` varchar(36) NOT NULL,
  `childNodeNumber` int(11) DEFAULT NULL,
  `giveReason` bit(1) DEFAULT NULL,
  `name` varchar(190) DEFAULT NULL,
  `parentId` varchar(36) DEFAULT NULL,
  `parentIdGroup` varchar(190) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reportType_1_idx` (`sort`)
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
  `platformUserId` varchar(90) DEFAULT NULL,
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
# Structure for table "specification"
#

CREATE TABLE `specification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration` int(11) DEFAULT NULL,
  `enable` bit(1) NOT NULL,
  `marketPrice` decimal(12,2) DEFAULT NULL,
  `membershipCardId` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `sellingPrice` decimal(12,2) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `specificationName` varchar(100) DEFAULT NULL,
  `stock` bigint(20) DEFAULT NULL,
  `stockOccupy` bigint(20) DEFAULT NULL,
  `unit` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `specification_1_idx` (`membershipCardId`,`sort`)
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
  `topicUnhidePlatformShareProportion` int(11) DEFAULT NULL,
  `fileSecureLinkExpire` bigint(20) DEFAULT NULL,
  `fileSecureLinkSecret` varchar(190) DEFAULT NULL,
  `allowAnswer` bit(1) NOT NULL,
  `allowQuestion` bit(1) NOT NULL,
  `answerEditorTag` longtext,
  `answerReply_review` int(11) DEFAULT NULL,
  `answerReply_rewardPoint` bigint(20) DEFAULT NULL,
  `answer_review` int(11) DEFAULT NULL,
  `answer_rewardPoint` bigint(20) DEFAULT NULL,
  `answer_submitQuantity` int(11) DEFAULT NULL,
  `questionEditorTag` longtext,
  `question_review` int(11) DEFAULT NULL,
  `question_rewardPoint` bigint(20) DEFAULT NULL,
  `question_submitQuantity` int(11) DEFAULT NULL,
  `realNameUserAllowAnswer` bit(1) NOT NULL,
  `realNameUserAllowQuestion` bit(1) NOT NULL,
  `maxQuestionTagQuantity` int(11) DEFAULT NULL,
  `questionRewardPlatformShareProportion` int(11) DEFAULT NULL,
  `questionRewardAmountMax` decimal(12,2) DEFAULT NULL,
  `questionRewardAmountMin` decimal(12,2) NOT NULL,
  `questionRewardPointMax` bigint(20) DEFAULT NULL,
  `questionRewardPointMin` bigint(20) DEFAULT NULL,
  `allowRegisterAccount` longtext,
  `giveRedEnvelopeAmountMax` decimal(12,2) DEFAULT NULL,
  `giveRedEnvelopeAmountMin` decimal(12,2) NOT NULL,
  `allowReport` bit(1) NOT NULL,
  `report_submitQuantity` int(11) DEFAULT NULL,
  `showIpAddress` bit(1) NOT NULL,
  `reportMaxImageUpload` int(11) DEFAULT NULL,
  `supportEditor` int(11) DEFAULT NULL,
  `topicHeatFactor` varchar(255) DEFAULT NULL,
  `topicHotRecommendedTime` int(11) DEFAULT NULL,
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
  `verifyCSRF` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "thirdpartylogininterface"
#

CREATE TABLE `thirdpartylogininterface` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dynamicParameter` longtext,
  `enable` bit(1) NOT NULL,
  `interfaceProduct` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `supportEquipment` varchar(10) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  `giveRedEnvelopeId` varchar(32) DEFAULT NULL,
  `essence` bit(1) DEFAULT NULL,
  `isMarkdown` bit(1) DEFAULT NULL,
  `markdownContent` longtext,
  `weight` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topic_idx` (`tagId`,`status`),
  KEY `topic_3_idx` (`userName`,`postTime`),
  KEY `topic_5_idx` (`status`,`sort`,`lastReplyTime`),
  KEY `topic_6_idx` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


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
# Structure for table "topicunhideplatformshare"
#

CREATE TABLE `topicunhideplatformshare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platformShareProportion` int(11) DEFAULT NULL,
  `postUserName` varchar(80) DEFAULT NULL,
  `shareAmount` decimal(14,4) NOT NULL,
  `staff` bit(1) NOT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `totalAmount` decimal(12,2) NOT NULL,
  `unlockTime` datetime DEFAULT NULL,
  `unlockUserName` varchar(30) DEFAULT NULL,
  `postUserShareRunningNumber` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `topicUnhidePlatformShare_1_idx` (`unlockTime`)
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
  `deposit` decimal(14,4) DEFAULT NULL,
  `platformUserId` varchar(90) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `account` varchar(65) DEFAULT NULL,
  `cancelAccountTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmtrfpdps3j0ce18c8xglgjf7n` (`platformUserId`),
  UNIQUE KEY `UKdnq7r8jcmlft7l8l4j79l1h74` (`account`),
  KEY `user_idx` (`state`),
  KEY `user_2_idx` (`userName`)
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
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `answerId` bigint(20) DEFAULT NULL,
  `answerReplyId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `functionIdGroup` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_5_idx` (`functionIdGroup`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_1"
#

CREATE TABLE `userdynamic_1` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `answerId` bigint(20) DEFAULT NULL,
  `answerReplyId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `functionIdGroup` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_5_idx` (`functionIdGroup`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_2"
#

CREATE TABLE `userdynamic_2` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `answerId` bigint(20) DEFAULT NULL,
  `answerReplyId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `functionIdGroup` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_5_idx` (`functionIdGroup`,`userName`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#
# Structure for table "userdynamic_3"
#

CREATE TABLE `userdynamic_3` (
  `id` varchar(36) NOT NULL,
  `commentId` bigint(20) DEFAULT NULL,
  `module` int(11) DEFAULT NULL,
  `postTime` datetime DEFAULT NULL,
  `replyId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `quoteCommentId` bigint(20) DEFAULT NULL,
  `answerId` bigint(20) DEFAULT NULL,
  `answerReplyId` bigint(20) DEFAULT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `functionIdGroup` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userDynamic_1_idx` (`userName`,`status`,`postTime`),
  KEY `userDynamic_5_idx` (`functionIdGroup`,`userName`,`module`)
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
  KEY `userRoleGroup_3_idx` (`userRoleId`,`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;