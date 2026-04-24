job "${CI_PROJECT_NAME}-${NOMAD_ENV}" {
    datacenters = ["DCE"]
    namespace   = "${NOMAD_NAMESPACE}"

    # where can this job be deployed
    constraint {
        attribute = "${node.class}"
        value     = "${NODE_CLASS}"
    }

    meta {
        GIT-SHA = "${CI_COMMIT_SHORT_SHA}"
        VER     = "${CI_COMMIT_TAG}"
    }
    group "${CI_PROJECT_NAME}-group" {
        count = 1  # Controls the number of instances that is created af each "services"

        update {
            max_parallel      = 1
            health_check      = "checks"
            min_healthy_time  = "10s"
            healthy_deadline  = "5m"
            progress_deadline = "10m"
            auto_revert       = true  #This enables Nomad to revert to previous version if the deployment fails
            auto_promote      = false
            #canary            = 1 # Use this to make a "test" deploy of a new version. Use in combination with "count"
        }
        reschedule {
            delay          = "60s"
            delay_function = "exponential"
            max_delay      = "30m"
            unlimited      = true
        }
        restart {
            attempts = 3 # no. of retries in the given interval
            delay    = "10s" # before restarting after stopping it
            interval = "10m" # inside the interval only listed number of attempts is allowed
            mode     = "delay"
            # If "delay" a set of new attempts will be performed after the interval, if "fail" this will not happen (thus depending on the reschedule setting)
        }
        network {
            port "http" { to = 8080 }
        }
        task "service" {
            driver = "docker"

            env {
                MICRONAUT_ENVIRONMENTS = "${MICRONAUT_ENVIRONMENTS}"
            }
            config {
                image = "${HARBOR_HOST}/${HARBOR_PROJECT}/${CI_PROJECT_NAME}:${IMAGE_TAG}"
                dns_servers = ["${attr.unique.network.ip-address}"]

                auth {
                    password = "${HARBOR_PASS}"
                    username = "${HARBOR_USER}"
                }
                ports       = ["http"]
            }
            resources {
                cpu    = 100
                memory = 384
            }
            service {
                name = "${CI_PROJECT_NAME}-${NOMAD_ENV}"
                port = "http"
                tags = [
                    "traefik.enable=true",
                    "traefik.owner=${NOMAD_NAMESPACE}",
                    "traefik.http.routers.${CI_PROJECT_NAME}-${NOMAD_ENV}.tls=true",
                    "traefik.http.services.${CI_PROJECT_NAME}-${NOMAD_GROUP_NAME}-${NOMAD_ENV}.loadbalancer.sticky.cookie=${LOADBALANCER_STICKY_COOKIE}",
                    "traefik.expose_to_internet=${EXPOSE_TO_INTERNET}",
                    "traefik.http.routers.${CI_PROJECT_NAME}-${NOMAD_ENV}.rule=Host(`${NOMAD_ENV}-${CI_PROJECT_NAME}.${JAP_DNS_BASE}`)"
                ]
                check {
                    name     = "alive"
                    type     = "tcp"
                    interval = "10s"
                    timeout  = "2s"
                }
            }
        }

        task "logshipper" {
            driver = "docker"
            env {
                HUMIO_INGEST_TOKEN = "${HUMIO_INGEST_TOKEN}"
            }
            config {
                image       = "registry.gitlab.com/regionsyd/container-management/namespaces/jap/nomad-logshipper:latest"
                auth {
                    username = "gitlab-nomad-deploy-token"
                    password = "BdFb7k1h5a6n1312Aice"
                }
                dns_servers = ["10.10.10.10"]
            }
            resources {
                cpu    = 100
                memory = 128
            }
        }
    }
}