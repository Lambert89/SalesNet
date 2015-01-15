'use strict';

/**
 * @ngdoc function
 */
angular
		.module('webContentApp')
		.controller(
				'FileUploadCtrl',
				function($scope, FileUploader, SweetAlert, $http) {
					$scope.uploadCount = 0;

					var uploader = $scope.uploader = new FileUploader({
						url : 'api/admin/fileupload',
						alias : "files"
					});
					$scope.isProcessing = false;
					$scope.isFail = false;
					$scope.isSuccess = false;

					$scope.sendUpdateNotification = function() {
						$scope.isProcessing = true;
						$scope.isFail = false;
						$scope.isSuccess = false;
						$http
								.get("api/admin/confirmUpdateNotification")
								.success(
										function(data, status, headers, config) {
											$scope.isProcessing = false;
											$scope.isSuccess = true;
										})
								.error(function(data, status, headers, config) {
									$scope.isProcessing = false;
									$scope.isFail = true;
								});
					}

					// FILTERS

					uploader.filters.push({
						name : 'customFilter',
						fn : function(item /* {File|FileLikeObject} */,
								options) {
							var filename = item.name;
							return (filename.indexOf(".pdf", filename.length
									- ".pdf".length) !== -1);
						}
					});

					// CALLBACKS

					uploader.onWhenAddingFileFailed = function(
							item /* {File|FileLikeObject} */, filter, options) {
						console.info('onWhenAddingFileFailed', item, filter,
								options);
						SweetAlert
								.swal(
										'Oops...',
										'Only PDF files are support! Please ensure you only choose file with "pdf" as extension.',
										'error');
					};
					uploader.onAfterAddingFile = function(fileItem) {
						console.info('onAfterAddingFile', fileItem);
					};
					uploader.onAfterAddingAll = function(addedFileItems) {
						console.info('onAfterAddingAll', addedFileItems);
					};
					uploader.onBeforeUploadItem = function(item) {
						console.info('onBeforeUploadItem', item);
					};
					uploader.onProgressItem = function(fileItem, progress) {
						console.info('onProgressItem', fileItem, progress);
					};
					uploader.onProgressAll = function(progress) {
						console.info('onProgressAll', progress);
					};
					uploader.onSuccessItem = function(fileItem, response,
							status, headers) {
						console.info('onSuccessItem', fileItem, response,
								status, headers);
						SweetAlert.swal('Congratulations!',
								'File Upload Success!', 'success');
						$scope.uploadCount++;
					};
					uploader.onErrorItem = function(fileItem, response, status,
							headers) {
						console.info('onErrorItem', fileItem, response, status,
								headers);
						SweetAlert.swal('Oops...',
								'Something went wrong! Upload failded.',
								'error');
					};
					uploader.onCancelItem = function(fileItem, response,
							status, headers) {
						SweetAlert.swal({
							title : 'Are you sure?',
							text : 'Your will cancel the uploading file!',
							type : 'warning',
							showCancelButton : true,
							confirmButtonColor : '#DD6B55',
							confirmButtonText : 'Yes, cancell it!'
						}, function() {
							SweetAlert.swal('Cancell!');
							console.info('onCancelItem', fileItem, response,
									status, headers);
						});
					};
					uploader.onCompleteItem = function(fileItem, response,
							status, headers) {
						console.info('onCompleteItem', fileItem, response,
								status, headers);
					};
					uploader.onCompleteAll = function() {
						console.info('onCompleteAll');
					};

					console.info('uploader', uploader);
				})
		.controller(
				"UserDetailsCtrl",
				function($scope, $http) {
					$scope.isFail = false;
					$scope.userDetails = [];
					$http
							.get("api/admin/getUserDetails")
							.success(
									function(data, status, headers, config) {
										for ( var key in data) {
											var item = {};
											item.email = key;
											item.username = data[key].user.name;
											item.phoneNumber = data[key].user.phoneNumber;
											if (data[key].comment) {
												item.comment = data[key].comment.comment;
												item.date = data[key].comment.date;
											}
											item.active = data[key].user.isActive;
											$scope.userDetails.push(item);
										}
									}).error(
									function(data, status, headers, config) {
										$scope.isFail = true;
									});
				}).filter('UserDetailsFilter', function() {
			return function(userDetails, isActiveOnly, withPhoneNumberOnly) {
				var filtered = [];
				for (var i = 0; i < userDetails.length; i++) {
					var item = userDetails[i];
					if (isActiveOnly && withPhoneNumberOnly) {
						if (item.active && item.phoneNumber) {
							filtered.push(item);
						}
					} else if (isActiveOnly) {
						if (item.active) {
							filtered.push(item);
						}
					} else if (withPhoneNumberOnly) {
						if (item.phoneNumber) {
							filtered.push(item);
						}
					} else {
						filtered.push(item);
					}
				}
				return filtered;
			};
		});
