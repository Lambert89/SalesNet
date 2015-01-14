'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:UserCtrl
 * @description # UserCtrl Controller of the webContentApp
 */
angular.module('webContentApp').controller('UserCtrl', function($scope) {
	$scope.awesomeThings = [ 'HTML5 Boilerplate', 'AngularJS', 'Karma' ];
}).controller("ViewerCtrl", function($scope, $http, $element) {
	$scope.generateUrl = function(baseUrl, documentName, $event) {
                console.log("clicking the link: " + documentName);
		$scope.previewDocumentUrl = baseUrl + "/ViewerJS/#../api/download/" + documentName;
                $($element).find("iframe")[0].contentWindow.location.reload();
	};
	$http.get('api/documents').success(function(data, status, headers, config) {
		$scope.documents = data.documents;
		$scope.baseUrl = data.baseUrl;
		$scope.isError = false;
		if ($scope.documents.length > 0) {
			$scope.previewDocumentUrl = $scope.baseUrl + "/ViewerJS/#../api/download/" + $scope.documents[0];
		}
	}).error(function(data, status, headers, config) {
		$scope.isError = true;
	});
}).controller('InterestModalCtrl', function ($scope, $modal) {
  $scope.open = function (size) {
    var modalInstance = $modal.open({
      templateUrl: 'interestModal.html',
      controller: 'ModalInstanceCtrl'
    });
  };
}).controller('ModalInstanceCtrl', function ($scope, $modalInstance) {
  $scope.subscribe = function () {
    alert("subscribed!");
  };
});
