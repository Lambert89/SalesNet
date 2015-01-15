'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:UserCtrl
 * @description # UserCtrl Controller of the webContentApp
 */
angular.module('webContentApp').controller('UserCtrl', function($scope) {
	$scope.awesomeThings = [ 'HTML5 Boilerplate', 'AngularJS', 'Karma' ];
}).controller(
		"ViewerCtrl",
		function($scope, $http, $element) {
			$scope.generateUrl = function(baseUrl, documentName, $event) {
				console.log("clicking the link: " + documentName);
				$scope.previewDocumentUrl = baseUrl
						+ "/ViewerJS/#../api/download/" + documentName;
				$($element).find("iframe")[0].contentWindow.location.reload();
			};
			$http.get('api/documents').success(
					function(data, status, headers, config) {
						$scope.documents = data.documents;
						$scope.baseUrl = data.baseUrl;
						$scope.isError = false;
						if ($scope.documents.length > 0) {
							$scope.previewDocumentUrl = $scope.baseUrl
									+ "/ViewerJS/#../api/download/"
									+ $scope.documents[0];
						}
					}).error(function(data, status, headers, config) {
				$scope.isError = true;
			});
		}).controller('InterestModalCtrl', function($scope, $modal) {
	$scope.open = function() {
		var modalInstance = $modal.open({
			templateUrl : 'interestModal.html',
			controller : 'ModalInstanceCtrl'
		});
		modalInstance.result.then(function(teardown) {
			teardown();
		});
	};
}).controller(
		'ModalInstanceCtrl',
		function($scope, $modalInstance, $http, $interval) {
			$scope.unsubscribe = function(unsubscribeEmail) {
				$scope.isEnabled = false;
				$scope.isSuccess = false;
				$scope.isFailed = false;
				$scope.isProcessing = true;
				var targetUrl = location.href.replace("index.html",
						"api/unsubscribe");
				$http.post(targetUrl, {
					unsubscribeEmail : unsubscribeEmail
				}).success(function(data, status, headers, config) {
					console.log("success!");
					console.debug("data: ", data);
					console.debug("status: ", status);
					console.debug("headers: ", headers);
					console.debug("config: ", config);
					$scope.isSuccess = true;
					$scope.isFailed = false;
					$scope.isProcessing = false;
					$scope.timeRemain = 3;
					var interval = $interval(function() {
						if ($scope.timeRemain === 0) {
							$modalInstance.close(function() {
								$scope.isSuccess = false;
								$scope.isFailed = false;
								$scope.isProcessing = false;
							});
							$interval.cancel(interval);
						}
						$scope.timeRemain = $scope.timeRemain - 1;
						console.debug("$scope.timeRemain", $scope.timeRemain);
					}, 1000);
				}).error(function(data, status, headers, config) {
					console.log("failed!");
					console.debug("data: ", data);
					console.debug("status: ", status);
					console.debug("headers: ", headers);
					console.debug("config: ", config);
					$scope.isFailed = true;
					$scope.isSuccess = false;
					$scope.isProcessing = false;
				});
			};
			$scope.subscribe = function(subscribeEmail, userName, isContactMe,
					phoneNumber, otherThingsToSay) {
				$scope.isEnabled = false;
				$scope.isSuccess = false;
				$scope.isFailed = false;
				$scope.isProcessing = true;
				console.debug("subscribeEmail: ", subscribeEmail);
				var targetUrl = location.href.replace("index.html",
						"api/subscribe");
				$http.post(targetUrl, {
					mailAddress : subscribeEmail,
					userName : userName,
					contactMeIndicator : isContactMe,
					phoneNumber : phoneNumber,
					otherThingsToSay : otherThingsToSay
				}).success(function(data, status, headers, config) {
					console.log("success!");
					console.debug("data: ", data);
					console.debug("status: ", status);
					console.debug("headers: ", headers);
					console.debug("config: ", config);
					$scope.isSuccess = true;
					$scope.isFailed = false;
					$scope.isProcessing = false;
					$scope.timeRemain = 3;
					var interval = $interval(function() {
						if ($scope.timeRemain === 0) {
							$modalInstance.close(function() {
								$scope.isSuccess = false;
								$scope.isFailed = false;
								$scope.isProcessing = false;
							});
							$interval.cancel(interval);
						}
						$scope.timeRemain = $scope.timeRemain - 1;
						console.debug("$scope.timeRemain", $scope.timeRemain);
					}, 1000);
				}).error(function(data, status, headers, config) {
					console.log("failed!");
					console.debug("data: ", data);
					console.debug("status: ", status);
					console.debug("headers: ", headers);
					console.debug("config: ", config);
					$scope.isFailed = true;
					$scope.isSuccess = false;
					$scope.isProcessing = false;
				});
				console.debug("$modalInstance", $modalInstance);
			};
			$scope.isEnabled = true;
			$scope.isProcessing = false;
			$scope.isSuccess = false;
			$scope.isFailed = false;
		}).directive('numbersOnly', function() {
	return {
		require : 'ngModel',
		link : function(scope, element, attrs, modelCtrl) {
			modelCtrl.$parsers.push(function(inputValue) {
				// this next if is necessary for when using ng-required on your
				// input.
				// In such cases, when a letter is typed first, this parser will
				// be called
				// again, and the 2nd time, the value will be undefined
				if (inputValue == undefined)
					return ''
				var transformedInput = inputValue.replace(/[^0-9+]/g, '');
				if (transformedInput != inputValue) {
					modelCtrl.$setViewValue(transformedInput);
					modelCtrl.$render();
				}

				return transformedInput;
			});
		}
	};
}).directive('noTag', function() {
	return {
		require : 'ngModel',
		link : function(scope, element, attrs, modelCtrl) {
			modelCtrl.$parsers.push(function(inputValue) {
				// this next if is necessary for when using ng-required on your
				// input.
				// In such cases, when a letter is typed first, this parser will
				// be called
				// again, and the 2nd time, the value will be undefined
				if (inputValue == undefined)
					return ''
				var transformedInput = inputValue.replace(/[^<>]/g, '');
				if (transformedInput != inputValue) {
					modelCtrl.$setViewValue(transformedInput);
					modelCtrl.$render();
				}

				return transformedInput;
			});
		}
	};
});
