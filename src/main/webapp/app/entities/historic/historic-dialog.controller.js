(function() {
    'use strict';

    angular
        .module('jhipsterfourApp')
        .controller('HistoricDialogController', HistoricDialogController);

    HistoricDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Historic'];

    function HistoricDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Historic) {
        var vm = this;

        vm.historic = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.historic.id !== null) {
                Historic.update(vm.historic, onSaveSuccess, onSaveError);
            } else {
                Historic.save(vm.historic, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jhipsterfourApp:historicUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creationDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
