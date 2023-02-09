# ses-demo
Author: Brian Kinsella

This is a custom application template with: 
- Spring boot
- Github actions workflow
- Continuous integration - testing
- Docker build, 
- Auth with AWS
- Integration with Amazon ECR


To create a spring boot app from this template:
1. In github, navigate to main page of this repository (https://github.com/solenersync/ses-demo)
2. Select `Use this template`
3. Select `Create a new repository
4. Enter name of new repository, choose private

To create manifests:
1. Open repo https://github.com/solenersync/infrastructure
2. Copy over existing service dir (eg: ses-demo) and rename {newservice}
3. Update config in all manifests in new dir to new service name eg: {newservice} instead of {ses-demo}
4. Add pathtype for new service in ingress manifest

