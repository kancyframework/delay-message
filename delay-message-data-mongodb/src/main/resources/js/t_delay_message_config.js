/*
 Navicat Premium Data Transfer

 Source Server         : kancy.top_mongo
 Source Server Type    : MongoDB
 Source Server Version : 40106
 Source Host           : kancy.top:27017
 Source Schema         : test

 Target Server Type    : MongoDB
 Target Server Version : 40106
 File Encoding         : 65001

 Date: 25/07/2020 19:30:38
*/


// ----------------------------
// Collection structure for t_delay_message_config
// ----------------------------
db.getCollection("t_delay_message_config").drop();
db.createCollection("t_delay_message_config");
db.getCollection("t_delay_message_config").createIndex({
    messageKey: NumberInt("1")
}, {
    name: "uniq_messageKey",
    background: true,
    unique: true
});

// ----------------------------
// Documents of t_delay_message_config
// ----------------------------
db.getCollection("t_delay_message_config").insert( {
    _id: NumberInt(1),
    messageKey: "test_bean",
    messageType: "test_bean",
    tableName: "t_delay_message",
    noticeType: "BEAN",
    noticeAddress: "log",
    useCache: true,
    maxScanTimes: 5,
    aliveTime: "30d",
    createdTime: ISODate("2020-07-27T13:44:15.000Z"),
    updatedTime: ISODate("2020-07-27T13:44:27.000Z")
} );
db.getCollection("t_delay_message_config").insert( {
    _id: NumberInt(2),
    messageKey: "test_http",
    messageType: "test_http",
    tableName: "t_delay_message",
    noticeType: "HTTP",
    noticeAddress: "http://localhost:8080/callback",
    useCache: true,
    maxScanTimes: 5,
    aliveTime: "30d",
    createdTime: ISODate("2020-07-27T13:44:15.000Z"),
    updatedTime: ISODate("2020-07-27T13:44:27.000Z")
} );
db.getCollection("t_delay_message_config").insert( {
    _id: NumberInt(3),
    messageKey: "test_stream",
    messageType: "test_stream",
    tableName: "t_delay_message",
    noticeType: "STREAM",
    noticeAddress: "test.business",
    useCache: true,
    maxScanTimes: 5,
    aliveTime: "30d",
    createdTime: ISODate("2020-07-27T13:44:15.000Z"),
    updatedTime: ISODate("2020-07-27T13:44:27.000Z")
} );


