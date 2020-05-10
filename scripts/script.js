db = connect("localhost:27017/demo");
db.dropDatabase();
db.createCollection("user");
db.user.insert({
    accountName: 'root',
    password: '7557383aeb894a634176484c5e47f83218fc9636ece61d7c6e68e6626f2211293c4c2db2cb1745eae28829b8954f955cdec708cd2162dd9d509258cb2c3c417a',
    loggedIn: false,
    locked: false
});
