describe('models.js', function() {

  describe('TripPattern', function() {
    var tripPattern;

    var expectToBeInOrder = function() {
      _.each(tripPattern.get('patternStops'), function(ps, i) {
        expect(ps.stopSequence).toEqual(i+1);
      });
    };

    beforeEach(function () {
      tripPattern = new GtfsEditor.TripPattern({
        patternStops: [
          {stopSequence: 3, stop: 103},
          {stopSequence: 1, stop: 101},
          {stopSequence: 2, stop: 102}
        ]
      });
    });

    it('should have pattern stops', function() {
      expect(_.isArray(tripPattern.get('patternStops'))).toBe(true);
    });

    it('is always in the same order as the stopSequence attributes', function() {
      expectToBeInOrder();
    });

    it('can append a stop', function() {
      tripPattern.addStop({stop: 104});

      var ps = _.last(tripPattern.get('patternStops'));
      expect(ps.stop).toEqual(104);
      expectToBeInOrder();
    });

    it('can insert a stop at an index at the beginning', function() {
      tripPattern.insertStopAt({stop: 104}, 0);

      var ps = _.first(tripPattern.get('patternStops'));
      expect(ps.stop).toEqual(104);
      expect(ps.stopSequence).toEqual(1);
      expectToBeInOrder();
    });

    it('can insert a stop at an index at the end', function() {
      tripPattern.insertStopAt({stop: 104}, 3);

      var ps = _.last(tripPattern.get('patternStops'));
      expect(ps.stop).toEqual(104);
      expect(ps.stopSequence).toEqual(4);
      expectToBeInOrder();
    });

    it('can insert a stop at an index in the middle', function() {
      tripPattern.insertStopAt({stop: 104}, 1);

      var ps = tripPattern.get('patternStops')[1];
      expect(ps.stop).toEqual(104);
      expect(ps.stopSequence).toEqual(2);
      expectToBeInOrder();
    });

    it('can remove a stop at an index', function() {
      var removed = tripPattern.removeStopAt(1),
          patternStops = tripPattern.get('patternStops');

      expect(removed.stop).toEqual(102);
      expect(_.size(patternStops)).toEqual(2);
      expectToBeInOrder();
    });

    it('can move a stop from an index to an index', function() {
      tripPattern.moveStopTo(1, 0);

      var patternStops = tripPattern.get('patternStops');

      expect(_.size(patternStops)).toEqual(3);
      expect(patternStops[0].stop).toEqual(102);
      expect(patternStops[1].stop).toEqual(101);
      expect(patternStops[2].stop).toEqual(103);

      expectToBeInOrder();
    });
  });
});
