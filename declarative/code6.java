from typing import List
from random import randint, random, choices
from collections import namedtuple

# Input data (clean, no preprocessing needed)
courses = ['C1', 'C2', 'C3', 'C4']
teachers = ['T1', 'T2', 'T3']
rooms = ['R1', 'R2']
time_slots = ['M1', 'M2', 'T1', 'T2']

# Class to represent a course assignment
class CourseAssignment:
    def __init__(self, course: str, teacher: str, room: str, time_slot: str):
        self._course = course
        self._teacher = teacher
        self._room = room
        self._time_slot = time_slot

    @property
    def course(self) -> str:
        return self._course

    @property
    def teacher(self) -> str:
        return self._teacher

    @property
    def room(self) -> str:
        return self._room

    @property
    def time_slot(self) -> str:
        return self._time_slot

# Class to represent a schedule
class Schedule:
    def __init__(self, assignments: List[CourseAssignment]):
        self._assignments = assignments.copy()  # Ensure immutability

    @property
    def assignments(self) -> List[CourseAssignment]:
        return self._assignments.copy()

    def calculate_fitness(self) -> int:
        """Count conflicts (teacher/room overlaps in same time slot)."""
        conflicts = 0
        by_time = sorted(self._assignments, key=lambda x: x.time_slot)
        for time_slot, group in [(k, list(g)) for k, g in groupby(by_time, key=lambda x: x.time_slot)]:
            # Count teacher conflicts
            teachers = [a.teacher for a in group]
            conflicts += len(teachers) - len(set(teachers))
            # Count room conflicts
            rooms = [a.room for a in group]
            conflicts += len(rooms) - len(set(rooms))
        return conflicts

# Class to manage the genetic algorithm
class GeneticAlgorithm:
    def __init__(self, courses: List[str], teachers: List[str], rooms: List[str], time_slots: List[str], pop_size: int = 50):
        self._courses = courses
        self._teachers = teachers
        self._rooms = rooms
        self._time_slots = time_slots
        self._pop_size = pop_size
        self._population = self._generate_population()

    def _generate_schedule(self) -> Schedule:
        """Generate a random schedule."""
        assignments = [
            CourseAssignment(
                course=course,
                teacher=self._teachers[randint(0, len(self._teachers) - 1)],
                room=self._rooms[randint(0, len(self._rooms) - 1)],
                time_slot=self._time_slots[randint(0, len(self._time_slots) - 1)]
            )
            for course in self._courses
        ]
        return Schedule(assignments)

    def _generate_population(self) -> List[Schedule]:
        """Generate initial population of schedules."""
        return [self._generate_schedule() for _ in range(self._pop_size)]

    def _tournament_select(self, tournament_size: int = 3) -> Schedule:
        """Select a schedule using tournament selection."""
        candidates = choices(self._population, k=tournament_size)
        return min(candidates, key=lambda s: s.calculate_fitness())

    def _crossover(self, parent1: Schedule, parent2: Schedule, crossover_rate: float = 0.7) -> Schedule:
        """Perform crossover between two schedules."""
        if random() > crossover_rate:
            return Schedule(parent1.assignments)
        
        point = randint(1, len(parent1.assignments) - 1)
        new_assignments = parent1.assignments[:point] + parent2.assignments[point:]
        return Schedule(new_assignments)

    def _mutate(self, schedule: Schedule, mutation_rate: float = 0.1) -> Schedule:
        """Mutate a schedule."""
        if random() > mutation_rate:
            return Schedule(schedule.assignments)
        
        assignments = schedule.assignments
        index = randint(0, len(assignments) - 1)
        new_assignment = CourseAssignment(
            course=assignments[index].course,
            teacher=self._teachers[randint(0, len(self._teachers) - 1)],
            room=self._rooms[randint(0, len(self._rooms) - 1)],
            time_slot=self._time_slots[randint(0, len(self._time_slots) - 1)]
        )
        new_assignments = assignments[:index] + [new_assignment] + assignments[index + 1:]
        return Schedule(new_assignments)

    def evolve(self, generations: int) -> Schedule:
        """Evolve the population."""
        for _ in range(generations):
            new_population = []
            for _ in range(self._pop_size):
                parent1 = self._tournament_select()
                parent2 = self._tournament_select()
                offspring = self._crossover(parent1, parent2)
                offspring = self._mutate(offspring)
                new_population.append(offspring)
            self._population = new_population
        return min(self._population, key=lambda s: s.calculate_fitness())

    def solve(self) -> dict:
        """Solve timetable scheduling problem."""
        best_schedule = self.evolve(generations=100)
        fitness = best_schedule.calculate_fitness()
        
        return {
            'best_schedule': best_schedule.assignments,
            'conflicts': fitness,
            'is_optimal': fitness == 0
        }

# Run analysis
ga = GeneticAlgorithm(courses, teachers, rooms, time_slots)
result = ga.solve()

# Print results
print("Timetable Scheduling Solution:")
print(f"Number of Conflicts: {result['conflicts']}")
print(f"Optimal Schedule Found: {result['is_optimal']}")
print("Best Schedule:")
for assignment in result['best_schedule']:
    print(f"  {assignment.course}: Teacher={assignment.teacher}, Room={assignment.room}, Time={assignment.time_slot}")


