'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:UserCtrl
 * @description
 * # UserCtrl
 * Controller of the webContentApp
 */
angular.module('webContentApp').controller('UserCtrl', function($scope){
    $scope.awesomeThings = [ 'HTML5 Boilerplate', 'AngularJS', 'Karma' ];
}).controller("ViewerCtrl", function($scope, $http){
    $scope.generateUrl = function(baseUrl, documentName){
        return baseUrl + "/ViewerJS/#../api/download/" + documentName;
    };
    $http.get('api/documents').success(function(data, status, headers, config){
        $scope.documents = data.documents;
        $scope.baseUrl = data.baseUrl;
        $scope.isError = false;
    }).error(function(data, status, headers, config){
        $scope.isError = true;
    });
});
