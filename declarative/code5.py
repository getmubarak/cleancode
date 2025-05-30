from functools import reduce
from typing import List, Tuple, Callable
from random import randint, random, choices
from collections import namedtuple
from itertools import groupby

# Immutable data structures
CourseAssignment = namedtuple('CourseAssignment', ['course', 'teacher', 'room', 'time_slot'])

# Input data (clean, no preprocessing needed)
courses = ['C1', 'C2', 'C3', 'C4']
teachers = ['T1', 'T2', 'T3']
rooms = ['R1', 'R2']
time_slots = ['M1', 'M2', 'T1', 'T2']

# Pure function to generate a random schedule
def generate_schedule() -> List[CourseAssignment]:
    """Generate a random schedule for all courses."""
    return [
        CourseAssignment(
            course=course,
            teacher=teachers[randint(0, len(teachers) - 1)],
            room=rooms[randint(0, len(rooms) - 1)],
            time_slot=time_slots[randint(0, len(time_slots) - 1)]
        )
        for course in courses
    ]

# Pure function to generate initial population
def generate_population(pop_size: int) -> List[List[CourseAssignment]]:
    """Generate a population of random schedules."""
    return list(map(lambda _: generate_schedule(), range(pop_size)))

# Pure function to count conflicts in a schedule
def calculate_fitness(schedule: List[CourseAssignment]) -> int:
    """Count conflicts (teacher/room overlaps in same time slot)."""
    # Group by time slot
    by_time = sorted(schedule, key=lambda x: x.time_slot)
    grouped = [list(g) for _, g in groupby(by_time, key=lambda x: x.time_slot)]
    
    conflicts = 0
    for group in grouped:
        # Count teacher conflicts (same teacher in same time slot)
        teachers_in_slot = [assignment.teacher for assignment in group]
        teacher_conflicts = len(teachers_in_slot) - len(set(teachers_in_slot))
        
        # Count room conflicts (same room in same time slot)
        rooms_in_slot = [assignment.room for assignment in group]
        room_conflicts = len(rooms_in_slot) - len(set(rooms_in_slot))
        
        conflicts += teacher_conflicts + room_conflicts
    
    return conflicts

# Pure function for tournament selection
def tournament_select(population: List[List[CourseAssignment]], tournament_size: int = 3) -> List[CourseAssignment]:
    """Select a schedule using tournament selection."""
    candidates = choices(population, k=tournament_size)
    return reduce(
        lambda best, current: current if calculate_fitness(current) < calculate_fitness(best) else best,
        candidates,
        candidates[0]
    )

# Pure function for crossover
def crossover(parent1: List[CourseAssignment], parent2: List[CourseAssignment], crossover_rate: float = 0.7) -> List[CourseAssignment]:
    """Perform crossover between two schedules."""
    if random() > crossover_rate:
        return parent1[:]  # Return copy to maintain immutability
    
    # Single-point crossover
    point = randint(1, len(parent1) - 1)
    return parent1[:point] + parent2[point:]

# Pure function for mutation
def mutate(schedule: List[CourseAssignment], mutation_rate: float = 0.1) -> List[CourseAssignment]:
    """Mutate a schedule by randomly changing assignments."""
    if random() > mutation_rate:
        return schedule[:]  # Return copy to maintain immutability
    
    # Randomly change one assignment
    index = randint(0, len(schedule) - 1)
    new_assignment = CourseAssignment(
        course=schedule[index].course,
        teacher=teachers[randint(0, len(teachers) - 1)],
        room=rooms[randint(0, len(rooms) - 1)],
        time_slot=time_slots[randint(0, len(time_slots) - 1)]
    )
    return schedule[:index] + [new_assignment] + schedule[index + 1:]

# Recursive function to evolve population
def evolve_population(population: List[List[CourseAssignment]], generations: int, current_gen: int = 0) -> List[CourseAssignment]:
    """Recursively evolve population until termination."""
    if current_gen >= generations:
        # Return best schedule
        return reduce(
            lambda best, current: current if calculate_fitness(current) < calculate_fitness(best) else best,
            population,
            population[0]
        )
    
    # Create new population
    new_population = []
    for _ in range(len(population)):
        parent1 = tournament_select(population)
        parent2 = tournament_select(population)
        offspring = crossover(parent1, parent2)
        offspring = mutate(offspring)
        new_population.append(offspring)
    
    return evolve_population(new_population, generations, current_gen + 1)

# Main functional pipeline
def solve_timetable(pop_size: int = 50, generations: int = 100) -> dict:
    """Solve timetable scheduling using genetic algorithm."""
    # Generate initial population
    population = generate_population(pop_size)
    
    # Evolve to find best schedule
    best_schedule = evolve_population(population, generations)
    
    # Compute metrics
    fitness = calculate_fitness(best_schedule)
    
    return {
        'best_schedule': best_schedule,
        'conflicts': fitness,
        'is_optimal': fitness == 0
    }

# Run analysis
result = solve_timetable()

# Print results
print("Timetable Scheduling Solution:")
print(f"Number of Conflicts: {result['conflicts']}")
print(f"Optimal Schedule Found: {result['is_optimal']}")
print("Best Schedule:")
for assignment in result['best_schedule']:
    print(f"  {assignment.course}: Teacher={assignment.teacher}, Room={assignment.room}, Time={assignment.time_slot}")

