'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope', 'scoreService'];

function scoreController($scope, scoreService) {

  function getCandidateScore(listOfCandidatesLists) {

    var chart = c3.generate({
      bindto: '#scoreChart',
      data: {
        // x: 'x',
        columns: listOfCandidatesLists
      }/*,
      axis: {
        x: {
          type: 'timeseries',
          tick: {
            format: '%m-%d'
          }
        }
      }*/
    });

    return chart;
  }

  function getListOfCandidatesLists(response) {

    var xAxis = ['x'];
    var listOfCandidatesLists = [];

    var candidatesList = response;

    for (var i = 0; i < candidatesList.length; i++) {

      var candidate = candidatesList[i];
      var candidateTwitterId = candidate.target;
      var candidateScoreList = candidate.scores;

      var innerList = [candidateTwitterId];

      for (var j = 0; j < candidateScoreList.length; j++) {

        var scoreNode = candidateScoreList[j];

        if (j == 0) {
          var dateInMillis = scoreNode.date;
          var date = new Date(dateInMillis)
          var dayAndMonth = date.getDate() + '-' + date.getMonth();
          xAxis.push(dateInMillis);
        }

        var scoreValue = scoreNode.value;
        innerList.push(scoreValue);
      }

      listOfCandidatesLists.push(innerList);
    }

    // listOfCandidatesLists.unshift(xAxis);
    return listOfCandidatesLists;
  }

  function getAllTargetsScoreSuccess(response) {

    var listOfCandidatesLists = getListOfCandidatesLists(response);
    console.debug('****************************************** 1');
    console.debug(listOfCandidatesLists);
    console.debug('****************************************** 2');
    $scope.chart = getCandidateScore(listOfCandidatesLists);
  }

  function logError(response) {
    console.error(response);
  }

  $scope.init = function() {

    scoreService.getAllTargetsScore(getAllTargetsScoreSuccess, logError);
  }
}