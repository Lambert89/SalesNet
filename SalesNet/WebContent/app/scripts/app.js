'use strict';

/**
 * @ngdoc overview
 * @name webContentApp
 * @description
 * # webContentApp
 *
 * Main module of the application.
 */
angular.module('webContentApp', [ 'ngMessages', 'angularFileUpload', 'oitozero.ngSweetAlert' ]).config(
    function($sceDelegateProvider){
    	$sceDelegateProvider.resourceUrlWhitelist(['self']);
    });
