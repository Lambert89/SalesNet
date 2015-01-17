'use strict';

/**
 * @ngdoc function
 * @name webContentApp.controller:UserCtrl
 * @description # UserCtrl Controller of the webContentApp
 */
angular.module('webContentApp').controller('UserCtrl', function($scope, $modal, $location, $base64, gettextCatalog) {
	$scope.init = function() {
            if(location.search) {
                var languageCode = location.search.replace("?", "").split("=")[1];
                gettextCatalog.setCurrentLanguage(languageCode);
            }else {
               location.search = "?language=" + gettextCatalog.currentLanguage;
            }
            if($location.$$path === "/unsubscribe") {
                var targetMail = $base64.decode($location.$$hash);
                $scope.isUnsubscribe = true;
                var modalInstance = $modal.open({
		    templateUrl : 'interestModal.html',
		    controller : 'ModalInstanceCtrl',
                    scope : $scope,
                    resolve: {
                        isUnsubscribe: function () {
                            return $scope.isUnsubscribe;
                        },
                        unsubscribeMail : function() {
                            return targetMail;
                        },
                        gettextCatalog : function() {
                            return gettextCatalog;
                        }
                    }
	        });
            }
        }
}).controller(
		"ViewerCtrl",
		function($scope, $http, $element) {
			$scope.generateUrl = function(baseUrl, documentName, $event) {
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
		}).controller('InterestModalCtrl', function($scope, $modal, gettextCatalog) {
	$scope.open = function() {
		var modalInstance = $modal.open({
			templateUrl : 'interestModal.html',
			controller : 'ModalInstanceCtrl',
                        resolve: {
                            isUnsubscribe: function () {
                                return false;
                            },
                            unsubscribeMail : function() {
                                return null;
                            },
                            gettextCatalog : function() {
                                return gettextCatalog;
                            }
                        }
		});
		modalInstance.result.then(function(teardown) {
			teardown();
		});
	};
}).controller(
		'ModalInstanceCtrl',
		function($scope, $modalInstance, $http, $interval, isUnsubscribe, unsubscribeMail, gettextCatalog) {
                        if(isUnsubscribe) {
                            $scope.isUnsubscribe = true;
                            $scope.unsubscribeMailAddress = unsubscribeMail;
                        }else {
                            $scope.isUnsubscribe = false;
                        }
			$scope.unsubscribe = function(unsubscribeEmail) {
				$scope.isEnabled = false;
				$scope.isSuccess = false;
				$scope.isFailed = false;
				$scope.isProcessing = true;
				var targetUrl = location.href.replace("index.html",
						"api/unsubscribe");
				$http.post(targetUrl, {
					unsubscribeEmail : unsubscribeEmail,
                                        language : gettextCatalog.currentLanguage
				}).success(function(data, status, headers, config) {
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
					}, 1000);
				}).error(function(data, status, headers, config) {
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
				var targetUrl = location.href.replace("index.html",
						"api/subscribe");
				$http.post(targetUrl, {
					mailAddress : subscribeEmail,
					userName : userName,
					contactMeIndicator : isContactMe,
					phoneNumber : phoneNumber,
					otherThingsToSay : otherThingsToSay,
                                        language : gettextCatalog.currentLanguage
				}).success(function(data, status, headers, config) {
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
					}, 1000);
				}).error(function(data, status, headers, config) {
					$scope.isFailed = true;
					$scope.isSuccess = false;
					$scope.isProcessing = false;
				});
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
