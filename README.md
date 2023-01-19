## EV Charging - Shared

### A Library of shared features for the ev-charging Application

The project implements a Service that functions as a backend to outlet devices and mediates charging requests between the devices and main Application. Users would issue commands to begin or stop charging by presenting a physical token, the RFID token to the device or by submitting these commands in a mobile app. The App Backend -project (link) works as backend to such mobile app. These backends services would then communicate to each others via a Kinesis stream.

This Project works as playground for implementing applications using Scala ZIO, and their architectures with AWS Serverless.

### Perhaps most interestingly this project implements...

OutletStateMachine - a state machine of possible states and their transitions for an ev-charger Outlet Device. The App Backend and Outlet Backend -.project will use these states and their transitions within their internal logics and in the messages they pass between themselves.

### Installing

The library can be published as locally available by executing the command `sbt publishLocal`
