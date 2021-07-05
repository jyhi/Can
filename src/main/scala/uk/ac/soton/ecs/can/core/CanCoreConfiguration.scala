package uk.ac.soton.ecs.can.core

case class CanCoreConfiguration(
    programMemoryWords: Int,
    dataMemoryWords: Int,
    syncReadMemory: Boolean = true,
    regAfterBlockInitializer: Boolean = true,
    regBetweenRounds: Boolean = true,
    regAfterAdder: Boolean = true
)
