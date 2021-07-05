package uk.ac.soton.ecs.can.core

case class CanCoreConfiguration(
    programMemoryWords: Int,
    dataMemoryWords: Int,
    syncReadMemory: Boolean,
    regAfterBlockInitializer: Boolean,
    regBetweenRounds: Boolean,
    regAfterAdder: Boolean
)
