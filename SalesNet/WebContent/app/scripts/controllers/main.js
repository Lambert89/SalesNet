'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the webContentApp
 */
angular.module('webContentApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
