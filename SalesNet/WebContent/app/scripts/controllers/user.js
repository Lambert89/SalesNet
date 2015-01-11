'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:UserCtrl
 * @description
 * # UserCtrl
 * Controller of the webContentApp
 */
angular.module('webContentApp')
  .controller('UserCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  }).controller("ViewerCtrl", function($scope) {
	  
  });
