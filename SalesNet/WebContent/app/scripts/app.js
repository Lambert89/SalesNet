'use strict';

/**
 * @ngdoc overview
 * @name webContentApp
 * @description
 * # webContentApp
 *
 * Main module of the application.
 */
angular.module('webContentApp', [ 'ngMessages', 'angularFileUpload', 'oitozero.ngSweetAlert', 'ui.bootstrap', 'base64', 'gettext' ]).config(
    function($sceDelegateProvider){
    	$sceDelegateProvider.resourceUrlWhitelist(['self']);
    }).run(function (gettextCatalog) {
    gettextCatalog.setCurrentLanguage('zh');
}).controller("LanguageCtrl", function($scope, gettextCatalog) {
    $scope.checkLanguage = function(languageCode) {
        gettextCatalog.setCurrentLanguage(languageCode);
        location.search = "?language=" + languageCode;
    }
});
