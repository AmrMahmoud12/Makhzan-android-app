const functions = require('firebase-functions');
const admin=require('firebase-admin');


admin.initializeApp(functions.config().firebase);
const api = admin.firestore()
api.settings({timestampsInSnapshots: true})

//Function to upload url into the database
exports.onFileUpload = functions.storage.bucket().object().onFinalize(object => {
  const  str=object.name;
const parts = str.split('/');//split link of the image
const mySubString = parts[3];//get the ad id from the link
const picName=parts[4];//get the name of the  image which is x.png

const nameParts=picName.split('.');//split name of the image according to .
const requiredNamePart=nameParts[0];//get the 1st part which is the name without png

const id=parts[1];//get id of the user
    if (object.name.includes('/Ads_pictures/')) {
    const bucket = object.bucket;
    const filePath = object.name;
    const destBucket = admin.storage().bucket(bucket);
    const file = destBucket.file(filePath);
    return file.getSignedUrl({
        action: 'read',
        expires: '03-09-2491'
    }) .then(url => {
        const xx=url[0];
       // return console.log(""+requiredNamePart);
       //add pics to the User ads
        return admin.firestore().collection('Users').doc(id).collection('MyAds').doc(mySubString).collection('photos')
        .doc(requiredNamePart).set(
        {imageUrl :xx},
        {merge:true}
        ).then(()=>{
            //add pic to the user system 
            return admin.firestore().collection('System').doc(id).collection('ProfileAds')
            .doc(mySubString).collection('photos')
            .doc(requiredNamePart).set(
            {imageUrl :xx},
            {merge:true}
            ).then(()=>{
                //return console.log('i am here');
                return admin.firestore().collection('Users').doc(id).collection('MyAds').doc(mySubString).get().then(queryResult=>{
                    const categoryNameID=queryResult.data().category_NameID;
                    const countryID=queryResult.data().country_ID;
                    const adID=queryResult.data().ad_ID;
//    return console.log('category'+categoryNameID+'country_ID'+'ad_ID'+adID);
                    //add photos to the categories ads            
return admin.firestore().collection('MainCategories').doc(categoryNameID).collection('Countries').doc(countryID)
            .collection('Ads')
            .doc(adID)
            .collection('photos')
            .doc(requiredNamePart).set(
            {imageUrl :xx},
            {merge:true}
            ); 

                }).catch(function(){
                    return console.log('error3');
                });
            }).catch(function(){
                return console.log('error2');
            });

        }).catch(function(){
            return console.log("error1");
        });
        }).catch(function () {
            console.log(mySubString);
        });

  }else if (!object.name.includes('/Ads_pictures/')){
    console.log(`File ${object.name} is not a user picture. Ignoring it.`);
    return null;// fix an error
  }

});

//     exports.onFileUpload = functions.storage.bucket().object().onFinalize(object => {
//         var mySubString =  str.substring(
//             str.lastIndexOf("/Ads_pictures/") + 1, 
//             str.lastIndexOf("/")
//         );
//         if (object.name.includes('/Ads_pictures/')) {
//         const bucket = object.bucket;
//         const filePath = object.name;
//         const destBucket = admin.storage().bucket(bucket);
//         const file = destBucket.file(filePath);
//         return file.getSignedUrl({
//             action: 'read',
//             expires: '03-09-2491'
//         }) .then(url => {
//             const x=url[0];
//             return admin.firestore().collection('Users/{id}/MyAds/{AdId}/photos')
//             .doc().set(
        
//             {image :x},
//             {merge:true}
//             );
        
//             }).catch(function () {
//                 console.log('hhh');
//             });
//       }else if (!object.name.includes('/Ads_pictures/')){
//         console.log(`File ${object.name} is not a user picture. Ignoring it.`);
//         return null;
//       }
   
// });
  
 //working perfect 
//   exports.onFileUpload = functions.storage.bucket().object().onFinalize(object => {
//     if (object.name.startsWith('User_Pictures/')) {
//         const bucket = object.bucket;
//         const filePath = object.name;
//         const destBucket = admin.storage().bucket(bucket);
//         const file = destBucket.file(filePath);
//         return file.getSignedUrl({
//             action: 'read',
//             expires: '03-09-2491'
//         }) .then(url => {
//             const x=url[0];
//             return admin.firestore().collection('Users').doc('f3YZgDQEfshRKV8smitoTw9q2su1')
//             .collection('photos').doc().set(
        
//             {image :x},
//             {merge:true}
//             );
        
//             }).catch(function () {
//                 console.log('hhh');
//             });
//       }else if (!object.name.startsWith('User_Pictures/')){
//         console.log(`File ${object.name} is not a user picture. Ignoring it.`);
//         return null;
//       }
   
// }); 
 

    



exports.addnotify=functions.firestore.document('Users/{uid}').onCreate((snapshot,context)=>{
    const uid=context.params.uid;

    return admin.firestore().collection('Users').doc(uid).set({
        companiesnotifications: true,
        customersnotifications:true,
        paying:{
            paid :true
        }
          },
       { merge: true }
    );
});


exports.addToCategory=functions.firestore.document('Users/{userId}/MyAds/{MyAdId}').onWrite((change,context)=>{

    const userId=context.params.userId;
    const MyAdId=context.params.MyAdId;

  return admin.firestore().collection('Users').doc(userId).collection('MyAds').doc(MyAdId).get().then(queryResult=>{
    const ad_ID=queryResult.data().ad_ID;
    const category_NameID=queryResult.data().category_NameID;
    const companyName=queryResult.data().companyName;
    const country_ID=queryResult.data().country_ID;
    const creationDate=queryResult.data().creationDate;
    const myAdNametxt=queryResult.data().myAdNametxt;
    const myAddiscriptiontxt=queryResult.data().myAddiscriptiontxt;
    const phoneNumbera=queryResult.data().phoneNumbera;
    const puplisherId=queryResult.data().puplisherId;
    const trustedCompanyTxt=queryResult.data().trustedCompanyTxt;
    const myPlacestxt=queryResult.data().myPlacestxt

        return admin.firestore().collection('System').doc(userId).collection('ProfileAds').doc(MyAdId).set({
            ad_ID :ad_ID,
            category_NameID:category_NameID,
            companyName:companyName,
            country_ID:country_ID,
            creationDate:creationDate,
            myAdNametxt:myAdNametxt,
            myAddiscriptiontxt:myAddiscriptiontxt,
            phoneNumbera:phoneNumbera,
            puplisherId:puplisherId,
            myPlacestxt:myPlacestxt,
            trustedCompanyTxt:trustedCompanyTxt}).then(()=>{
                return console.log("successful");
                // return admin.firestore().collection('MainCategories/{catName}/Countries/{countryId}/Ads')
                // .doc(MyAdId).get()
            }).catch(function () {
                console.log("new function");
                });


      


  }).catch(function () {
    console.log(" Rejected");
    });
});

//POSITION OF THE AD 
exports.newfync=functions.firestore.document("System/{id}/ProfileAds/{adid}")
.onWrite((change,context)=>{
    const id=context.params.id;
    const adid=context.params.adid;
        //console.log("Users id  "+ id +"|notification id  "+ adid );

// return admin.firestore().collection('Users').where('companiesnotifications', '==', true).get().then(snapshot => {
//   return snapshot.forEach(doc => {
//     return console.log(doc.id, '=>', doc.data());
//     const xx=doc.id;


//   });
// }).catch(function () {
//     console.log("Promise Rejected");
// });


return admin.firestore().collection('System').doc(id).collection('ProfileAds').doc(adid)
.get().then(queryResult=>{
   const myAdNametxt=queryResult.data().myAdNametxt;
     const companyName=queryResult.data().companyName;
     const phoneNumbera=queryResult.data().phoneNumbera;
          const myPlacestxt=queryResult.data().myPlacestxt;

     const payload= {
                           notification: {
                               title : "وارد اليوم من : " + companyName,
                               body : myAdNametxt,
                               icon : "default",
                               click_action : "NOTIFICATIONTARGER.COM"
                           },
                           data :{
                               id :id,
                               title : "وارد اليوم من : " + companyName,
                               phoneNumbera :phoneNumbera,
                               myPlacestxt :myPlacestxt

                           }
                       };

                       return admin.messaging().sendToTopic(id,payload).then(result=>{
                       return console.log("Notification sent from "+ companyName +"and contains : "+ companyName);
                        
                   }).catch(function () {
                       console.log("Promise Rejected1");
                  });
}).catch(function () {
    console.log("Promise Rejected2");
});
        //INFORMATION FROM THE AD
        // return admin.firestore().collection('System').doc(id).collection('ProfileAds').doc(adid)
        // .get().then(queryResult=>{
        //     const myAdNametxt=queryResult.data().myAdNametxt;
        //     const companyName=queryResult.data().companyName;
        //    //return console.log("title " + myAdNametxt + "company name : " +companyName); 
        
        //     //const from=admin.firestore().collection('System').doc().collection('following').doc(adid).get();
        //     const payload= {
        //                     notification: {
        //                         title : "وارد اليوم من : " + companyName,
        //                         body : myAdNametxt,
        //                         icon : "default",
        //                         click_action : "NOTIFICATIONTARGER.COM"
        //                     },
        //                     data :{
        //                         id :id

        //                     }
        //                 };
 
        //                 return admin.messaging().sendToTopic(id,payload).then(result=>{
        //                     return console.log("Notification sent from "+ companyName +"and contains : "+ companyName);
        //                 });
        // });
    });

  /*exports.newfync=functions.firestore.document("System/{id}/ProfileAds/{adid}")
.onWrite((change,context)=>{
    const id=context.params.id;
    const adid=context.params.adid;
        //console.log("Users id  "+ id +"|notification id  "+ adid );


        //INFORMATION FROM THE AD
        return admin.firestore().collection('System').doc(id).collection('ProfileAds').doc(adid)
        .get().then(queryResult=>{
            const myAdNametxt=queryResult.data().myAdNametxt;
            const companyName=queryResult.data().companyName;
           //return console.log("title " + myAdNametxt + "company name : " +companyName); 
        
            //const from=admin.firestore().collection('System').doc().collection('following').doc(adid).get();
            const payload= {
                            notification: {
                                title : "وارد اليوم من : " + companyName,
                                body : myAdNametxt,
                                icon : "default",
                                click_action : "NOTIFICATIONTARGER.COM"
                            },
                            data :{
                                id :id

                            }
                        };
 
                        return admin.messaging().sendToTopic(id,payload).then(result=>{
                            return console.log("Notification sent from "+ companyName +"and contains : "+ companyName);
                        });
        });
    });
 */
  // return admin.firestore().collection('System').doc(followingid).collection('ProfileAds').doc().get().then(queryResult=>{
        //const myAdNametxt=queryResult.data().myAdNametxt;
        // const myPlacestxt=queryResult.data().myPlacestxt;

    //     const fromdata=admin.firestore().collection('System').doc(id).collection('following').doc(followingid).get();
    //     const todata=admin.firestore().collection('Users').doc(id).get();
    //     return Promise.all([fromdata,todata]).then(result=>{
    //         const followingName=result[0].data().followingName;
    //         const token_id=result[1].data().token_id;
    //         const payload= {
    //             notification: {
    //                 title : "notification from" +followingName,
    //                 body : myPlacestxt,
    //                 icon : "default"
    //                 //click_action : "NOTIFICATIONTARGER.COM"
    //             }
    //         };
    //         return admin.messaging().sendToDevice(token_id,payload).then(result=>{
    //             return console.log("NOTIFICATION SENT.");
    //     });
    // });
// });

// exports.fuync=functions.firestore.document("Users/{userid}/notification/{notification_id}")
// .onWrite((change,context)=>{
//     const userid=context.params.userid;
//     const notification_id=context.params.notification_id;
//    // console.log("Users id"+ userid +"|notification id"+ notification_id );
//     return admin.firestore().collection('Users')
//     .doc(userid).collection('notification').doc(notification_id).get().then(queryResult=>{
//         const fromuserid=queryResult.data().from;
//         const frommessage=queryResult.data().message;

//         const fromdata=admin.firestore().collection('Users').doc(fromuserid).get();
//         const todata=admin.firestore().collection('Users').doc(userid).get();
        
//         return Promise.all([fromdata,todata]).then(result=>{
//             const fromname=result[0].data().name;
//             const toname=result[1].data().name;
//             const tokenid=result[1].data().token_id;
//            //return console.log("from :" +fromname + "TO: " +toname);
           
//            const payload= {
//                notification: {
//                    title : "notification from" +fromname,
//                    body : frommessage,
//                    icon : "default",
//                    click_action : "NOTIFICATIONTARGER.COM"
//                },
//                data :{
//                    message : frommessage,
//                    fromuserid :fromuserid
//                }
//            };
//            return admin.messaging().sendToDevice(tokenid,payload).then(result=>{
//                return console.log("NOTIFICATION SENT.");
//            });
//         });
        
//     });
// });
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.fync=functions.firestore.document('System/{id}/ProfileOrders/{orderid}')
.onWrite((change,context)=>{
    const orderid=context.params.orderid;
    const id=context.params.id;

    // return admin.firestore().collection('Users').where('customersnotifications', '==', true).get().then(snapshot => {
    //     return snapshot.forEach(doc => {
          
    //     });
    // }).catch(function () {
    //     console.log("Promise Rejected");
    // });


    return admin.firestore().collection('System').doc(id).collection('ProfileOrders')
    .doc(orderid).get().then(queryResult=>{
        const companyName=queryResult.data().companyName;
        const orderTitle=queryResult.data().orderTitle;

        const payload= {
            notification :{
                title : "مطلوب أوردر من : " +companyName,
                body : orderTitle,
                icon : "default",
                click_action : "NOTIFICATIONT.COM"         
               },
            data : {
                    id :id
            }
        };
        const x=id+"1";
        return admin.messaging().sendToTopic(x,payload).then(result=>{
            return console.log("Order sent fr om "+ id + " and contains : "+ orderTitle);

}).catch(function () {
    console.log("Promise Rejected3");
});


}).catch(function () {
console.log("Promise Rejected4");



});
});
